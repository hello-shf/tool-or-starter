package com.my.es.service;

import com.my.es.constant.EsConstant;
import com.my.es.entity.ESParamEntity;
import lombok.Data;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


@Data
public class ESSearchService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TransportClient client;
    /**
     * 创建索引
     * @param esParamEntity
     * @return
     */
    public boolean buildIndex(ESParamEntity esParamEntity) {
        if (!isIndexExist(esParamEntity)) {
            log.info("Index is not exits!");
        }
        CreateIndexResponse buildIndexresponse = client.admin().indices().prepareCreate(esParamEntity.getIndex()).execute().actionGet();
        log.info(" 创建索引的标志: " + buildIndexresponse.isAcknowledged());
        return buildIndexresponse.isAcknowledged();
    }

    /**
     * 删除索引
     *
     * @param esParamEntity
     * @return
     */
    public boolean deleteIndex(ESParamEntity esParamEntity) {
        if (!isIndexExist(esParamEntity)) {
            log.error(" 索引不存在 ！！！！！!");
        }
        DeleteIndexResponse diResponse = client.admin().indices().prepareDelete(esParamEntity.getIndex()).execute().actionGet();
        if (diResponse.isAcknowledged()) {
            log.info("删除索引**成功** index->>>>>>>" + esParamEntity.getIndex());
        } else {
            log.error("删除索引**失败** index->>>>> " + esParamEntity.getIndex());
        }
        return diResponse.isAcknowledged();
    }

    /**
     * 查询数据
     * @param esParamEntity
     * index 索引<----->关系型数据库
     * type  类型<----->关系型数据表
     * id    数据ID<----->id
     * @return
     */
    public Map<String, Object> searchDataByParam(ESParamEntity esParamEntity) {
        if(esParamEntity.getIndex() == null || esParamEntity.getType() == null || esParamEntity.getEsId() == null) {
            log.error(" 无法查询数据，缺唯一值!!!!!!! ");
            return null;
        }
        //来获取查询数据信息
        GetRequestBuilder getRequestBuilder = client.prepareGet(esParamEntity.getIndex(), esParamEntity.getType(), esParamEntity.getEsId());
        GetResponse getResponse = getRequestBuilder.execute().actionGet();
        //这里也有指定的时间获取返回值的信息，如有特殊需求可以
        return getResponse.getSource();
    }

    /**
     * 更新数据
     *
     * data  添加的数据类型 json格式的
     * index 索引<----->关系型数据库
     * type  类型<----->关系型数据表
     * id    数据ID<----->id
     * @return
     */
    public void updateDataById(ESParamEntity esParamEntity) {
        if(esParamEntity.getIndex() == null || esParamEntity.getType() == null || esParamEntity.getEsId() == null) {
            log.error(" 无法更新数据，缺唯一值!!!!!!! ");
            return;
        }

        //更新步骤
        UpdateRequest up = new UpdateRequest();
        up.index(esParamEntity.getIndex()).type(esParamEntity.getType()).id(esParamEntity.getEsId()).doc(esParamEntity.getJsonObj());

        //获取响应信息
        //.actionGet(timeoutMillis)，也可以用这个方法，当过了一定的时间还没得到返回值的时候，就自动返回。
        UpdateResponse response = client.update(up).actionGet();
        log.info("更新数据状态信息，status{}"+response.status().getStatus());
    }

    /**
     * 添加数据
     *
     *  data  添加的数据类型 json格式的
     *  index 索引<----->关系型数据库
     *  type  类型<----->关系型数据表
     *  id    数据ID<----->id
     * @return
     */
    public String addTargetDataALL(ESParamEntity esParamEntity) {
        //判断一下次id是否为空，为空的话就设置一个id
        if(esParamEntity.getEsId() == null) {
            esParamEntity.setEsId(UUID.randomUUID().toString());
        }
        //正式添加数据进去
        IndexResponse response = client.prepareIndex(esParamEntity.getIndex(), esParamEntity.getType(), esParamEntity.getEsId()).setSource(esParamEntity.getJsonObj()).get();
        log.info("addTargetDataALL 添加数据的状态:{}"+response.status().getStatus());
        return response.getId();
    }

    /**
     * 通过ID删除数据
     *  index 索引，类似数据库
     *  type  类型，类似表
     *  id    数据ID
     */
    public void delDataById(ESParamEntity esParamEntity) {

        if(esParamEntity.getIndex() == null || esParamEntity.getType() == null || esParamEntity.getEsId() == null) {
            log.error(" 无法删除数据，缺唯一值!!!!!!! ");
            return;
        }
        //开始删除数据
        DeleteResponse response = client.prepareDelete(esParamEntity.getIndex(), esParamEntity.getType(), esParamEntity.getEsId()).execute().actionGet();
        log.info("删除数据状态，status-->>>>{},"+response.status().getStatus());
    }

    /**
     * 判断索引是否存在
     * @return
     */
    public boolean isIndexExist(ESParamEntity esParamEntity) {
        IndicesExistsResponse iep = client.admin().indices().exists(new IndicesExistsRequest(esParamEntity.getIndex())).actionGet();
        if (iep.isExists()) {
            log.info("此索引 [" + esParamEntity.getIndex() + "] 已经在ES集群里存在");
        } else {
            log.info(" 没有此索引 [" + esParamEntity.getIndex() + "] ");
        }
        return iep.isExists();
    }

    /**
     * 查询文档
     * @param esParamEntity
     * @return
     * @throws Exception
     */
    public SearchResponse searchEsObject(ESParamEntity esParamEntity){
        if (!isIndexExist(esParamEntity)) {
            log.error("Index is not exits!");
        }
        SearchRequestBuilder srb=client.prepareSearch(esParamEntity.getIndex()).setTypes(esParamEntity.getType());
        BoolQueryBuilder bb=QueryBuilders.boolQuery();
        //精确查找的map
        if(esParamEntity.getQueryMap()!=null){
            Map<String,Object> queryMap = esParamEntity.getQueryMap();
            for (String key : queryMap.keySet()){
                QueryBuilder queryBuilder = QueryBuilders.termQuery(key,queryMap.get(key));
                bb=bb.must(queryBuilder);
            }
        }
        //其他查找的list
        if(esParamEntity.getQueryList()!=null){
            List<Map<String,Object>> queryList= esParamEntity.getQueryList();
            Map<String,Map<String,Object>> rangeMap=new HashMap<String,Map<String,Object>>();
            for (Map<String, Object> map : queryList) {
                //判断查询类型
                switch (map.get("queryType").toString()){
                    case EsConstant.QUERY_TYPE_FUZZY://模糊匹配
                        QueryBuilder queryBuilderFuzzy = QueryBuilders.fuzzyQuery(map.get("key").toString(),map.get("value").toString());
                        //searchRequestBuilder.setQuery(queryBuilderFuzzy);
                        break;
                    case EsConstant.QUERY_TYPE_IS://精确匹配
                        QueryBuilder queryBuilderIs = QueryBuilders.matchQuery(map.get("key").toString(),map.get("value").toString());
                        bb=bb.must(queryBuilderIs);
                        //searchRequestBuilder.setQuery(queryBuilderIs);
                        break;
                    case EsConstant.QUERY_TYPE_LT://小于
                        QueryBuilder queryBuilderLt = QueryBuilders.rangeQuery(map.get("key").toString()).gt(map.get("value").toString());
                        bb=bb.filter(queryBuilderLt);
                        //searchRequestBuilder.setQuery(queryBuilderLt);
                        break;
                    case EsConstant.QUERY_TYPE_GT://大于
                        QueryBuilder queryBuilderGt = QueryBuilders.rangeQuery(map.get("key").toString()).gt(map.get("value").toString());
                        bb=bb.filter(queryBuilderGt);
                        //searchRequestBuilder.setQuery(queryBuilderGt);
                        break;
                    case EsConstant.QUERY_TYPE_RANGE_BEGIN://范围开始
                        String key = map.get("key").toString();
                        Map<String, Object> stringObjectMap = rangeMap.get(key);
                        if(stringObjectMap==null){
                            stringObjectMap = new HashMap<>();
                        }
                        stringObjectMap.put("begin",map.get("value").toString());
                        rangeMap.put(key,stringObjectMap);
                        break;
                    case EsConstant.QUERY_TYPE_RANGE_END://范围结束
                        String key1 = map.get("key").toString();
                        Map<String, Object> stringObjectMap1 = rangeMap.get(key1);
                        if(stringObjectMap1==null){
                            stringObjectMap1 = new HashMap<>();
                        }
                        stringObjectMap1.put("end",map.get("value").toString());
                        rangeMap.put(key1,stringObjectMap1);
                        break;
                }
            }
            //开始处理范围查询
            Set<String> keySet = rangeMap.keySet();
            for (String s : keySet) {
                String begin = rangeMap.get(s).get("begin").toString();
                String end = rangeMap.get(s).get("end").toString();
                RangeQueryBuilder to = QueryBuilders.rangeQuery(s).from(begin).to(end);
                bb=bb.filter(to);
                //searchRequestBuilder.setQuery(to);
            }
        }
        SearchRequestBuilder searchRequestBuilder = srb.setQuery(bb);
        if(esParamEntity.isPage()){
            searchRequestBuilder.setFrom(esParamEntity.getForm()).setSize(esParamEntity.getSize());
        }
        SearchResponse sr = searchRequestBuilder.addSort(esParamEntity.getSort(), esParamEntity.isSortDesc() ? SortOrder.DESC : SortOrder.ASC)
                .execute()
                .actionGet();
        log.info("数据总数："+sr.getHits().totalHits);
        return sr;
    }

    /**
     * 查询删除
     * @param esParamEntity
     */
    public void deleteBySearch(ESParamEntity esParamEntity){
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        SearchResponse response = searchEsObject(esParamEntity);
        if(response.getHits().getTotalHits()==0){
            log.error("查询删除数据为空！！");
            return;
        }
        for(SearchHit hit : response.getHits()){
            String id = hit.getId();
            bulkRequest.add(client.prepareDelete(esParamEntity.getIndex(), esParamEntity.getType(), id).request());
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            for(BulkItemResponse item : bulkResponse.getItems()){
                log.error("查询删除异常："+item.getFailureMessage());
            }
        }else {
            log.info("查询删除成功");
        }
    }

}