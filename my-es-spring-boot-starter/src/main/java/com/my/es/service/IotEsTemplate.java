package com.my.es.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.my.es.constant.EsConstant;
import com.my.es.entity.*;
import lombok.Data;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.InternalMin;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


@Data
public class IotEsTemplate {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 客户端
     */
    private TransportClient client;
    /**
     * 判断索引是否存在
     * @return
     */
    public boolean isIndexExist(String index) {
        if(StrUtil.isEmpty(index)){
            log.error("查询索引：Index is null");
        }
        IndicesExistsResponse iep = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
        return iep.isExists();
    }
    /**
     * 创建索引
     * @param index
     * @return
     */
    public boolean buildIndex(String index) {
        if (isIndexExist(index)) {
            log.error("Index is exits!");
        }
        CreateIndexResponse buildIndexresponse = client.admin().indices().prepareCreate(index).execute().actionGet();
        log.info(" 创建索引的标志: " + buildIndexresponse.isAcknowledged());
        return buildIndexresponse.isAcknowledged();
    }
    /**
     * 删除索引
     * @param index
     * @return
     */
    public boolean deleteIndex(String index) {
        if (!isIndexExist(index)) {
            log.error("删除索引 索引不存在 ！！！！！!");
        }
        DeleteIndexResponse diResponse = client.admin().indices().prepareDelete(index).execute().actionGet();
        if (diResponse.isAcknowledged()) {
            log.info("删除索引**成功** index->>>>>>>" + index);
        } else {
            log.error("删除索引**失败** index->>>>> " + index);
        }
        return diResponse.isAcknowledged();
    }

    /**
     * 添加数据
     *  data  添加的数据类型 json格式的
     *  index 索引<----->关系型数据库
     *  type  类型<----->关系型数据表
     *  id    数据ID<----->id
     * @return
     */
    public String addEsData(EsOtherEntity esOtherEntity) {
        //正式添加数据进去
        IndexResponse response = client.prepareIndex(esOtherEntity.getIndex(), esOtherEntity.getType(), esOtherEntity.getEsId()).setSource(esOtherEntity.getJsonObj()).get();
        log.info("addEsData 添加数据的状态:{}"+response.status().getStatus());
        return response.getId();
    }

    /**
     * 批量添加数据
     * @param esOtherEntity
     * @return
     */
    public void addEsDataBatch(EsOtherEntity esOtherEntity) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        JSONArray jsonObjList = esOtherEntity.getJsonObjList();
        for (Object o : jsonObjList) {
            bulkRequest.add(client.prepareIndex(esOtherEntity.getIndex(), esOtherEntity.getType(),null).setSource(JSONUtil.parseObj(o)));
        }
        //正式添加数据进去
        BulkResponse bulkResponse = bulkRequest.get();
        log.info("addEsDataBatch 添加数据的状态:{}"+bulkResponse.status().getStatus());
    }
    /**
     * 通过ID删除数据
     *  index 索引，类似数据库
     *  type  类型，类似表
     *  id    数据ID
     */
    public void delEsDataById(EsOtherEntity esOtherEntity) {
        if(esOtherEntity.getIndex() == null || esOtherEntity.getType() == null || esOtherEntity.getEsId() == null) {
            log.error(" 无法删除数据，缺唯一值!!!!!!! ");
            return;
        }
        //开始删除数据
        DeleteResponse response = client.prepareDelete(esOtherEntity.getIndex(), esOtherEntity.getType(), esOtherEntity.getEsId()).execute().actionGet();
        log.info("删除数据状态，status-->>>>{},"+response.status().getStatus());
    }
    /**
     * 查询删除
     * @param esOtherEntity
     */
    public void delEsDataBySearch(EsOtherEntity esOtherEntity){
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        EsSearchEntity esSearchEntity = esOtherEntity.getEsSearchEntity();
        SearchResponse response = searchResponse(esSearchEntity.index(esOtherEntity.getIndex()).type(esOtherEntity.getType()));
        if(response.getHits().getTotalHits()==0){
            log.error("查询删除数据为空,无法删除！！");
            return;
        }
        for(SearchHit hit : response.getHits()){
            String id = hit.getId();
            bulkRequest.add(client.prepareDelete(esOtherEntity.getIndex(), esOtherEntity.getType(), id).request());
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

    /**
     * 更新数据
     *
     * data  添加的数据类型 json格式的
     * index 索引<----->关系型数据库
     * type  类型<----->关系型数据表
     * id    数据ID<----->id
     * @return
     */
    public void updateEsDataById(EsOtherEntity esOtherEntity) {
        if(esOtherEntity.getIndex() == null || esOtherEntity.getType() == null || esOtherEntity.getEsId() == null) {
            log.error(" 无法更新数据，缺唯一值!!!!!!! ");
            return;
        }
        //更新步骤
        UpdateRequest up = new UpdateRequest();
        up.index(esOtherEntity.getIndex()).type(esOtherEntity.getType()).id(esOtherEntity.getEsId()).doc(esOtherEntity.getJsonObj());
        //获取响应信息
        //.actionGet(timeoutMillis)，也可以用这个方法，当过了一定的时间还没得到返回值的时候，就自动返回。
        UpdateResponse response = client.update(up).actionGet();
        log.info("更新数据状态信息，status{}"+response.status().getStatus());
    }

    /**
     * 查询更新数据
     * data  添加的数据类型 json格式的
     * index 索引<----->关系型数据库
     * type  类型<----->关系型数据表
     * id    数据ID<----->id
     * @return
     */
    public void updateEsDataBySearch(EsOtherEntity esOtherEntity) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        EsSearchEntity esSearchEntity = esOtherEntity.getEsSearchEntity();
        SearchResponse response = searchResponse(esSearchEntity.index(esOtherEntity.getIndex()).type(esOtherEntity.getType()));
        if(response.getHits().getTotalHits()==0){
            log.error("查询更新数据为空,无法更新！！");
            return;
        }
        for(SearchHit hit : response.getHits()){
            String id = hit.getId();
            bulkRequest.add(client.prepareUpdate(esOtherEntity.getIndex(),esOtherEntity.getType(), id).setDoc(esOtherEntity.getJsonObj()));
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            for(BulkItemResponse item : bulkResponse.getItems()){
                log.error("查询更新异常："+item.getFailureMessage());
            }
        }else {
            log.info("查询更新成功");
        }
    }


    /**
     * 查询文档
     * @param esSearchEntity
     * @return
     * @throws Exception
     */
    public SearchResponse searchResponse(EsSearchEntity esSearchEntity) {
        SearchRequestBuilder srb = getSearchRequestBuilder(esSearchEntity);
        if(srb==null){
            return null;
        }
        SearchResponse sr = srb.execute().actionGet();
        return sr;
    }

    /**
     * 封装查询条件
     * @param esSearchEntity
     * @return
     */
    private SearchRequestBuilder getSearchRequestBuilder(EsSearchEntity esSearchEntity){
        if (!isIndexExist(esSearchEntity.getIndex())) {
            log.error("Index is not exits!");
            return null;
        }
        SearchRequestBuilder srb = client.prepareSearch(esSearchEntity.getIndex()).setTypes(esSearchEntity.getType());
        //创建符合查询条件
        BoolQueryBuilder bb = QueryBuilders.boolQuery();
        //获取查询条件
        List<EsCondition> esConditionList = esSearchEntity.getEsConditionList();
        if (esConditionList != null) {
            for (EsCondition esCondition : esConditionList) {
                //判断查询类型
                switch (esCondition.getQueryType()) {
                    case EsConstant.QUERY_TYPE_EXISTS://字段必须存在
                        QueryBuilder queryBuilderExists = QueryBuilders.existsQuery(esCondition.getKey());
                        bb = bb.must(queryBuilderExists);
                        break;
                    case EsConstant.QUERY_TYPE_NOTEXISTS://字段不存在
                        QueryBuilder queryBuilderNotExists = QueryBuilders.existsQuery(esCondition.getKey());
                        bb = bb.mustNot(queryBuilderNotExists);
                        break;
                    case EsConstant.QUERY_TYPE_IS://精确匹配
                        QueryBuilder queryBuilderIs = QueryBuilders.matchQuery(esCondition.getKey(), esCondition.getValue());
                        bb = bb.must(queryBuilderIs);
                        //searchRequestBuilder.setQuery(queryBuilderIs);
                        break;
                    case EsConstant.QUERY_TYPE_PREFIX://前缀查询
                        QueryBuilder queryBuilderPrefix = QueryBuilders.prefixQuery(esCondition.getKey(), esCondition.getValue().toString());
                        bb = bb.filter(queryBuilderPrefix);
                        break;
                    case EsConstant.QUERY_TYPE_WILDCARD://通配符查询
                        QueryBuilder queryBuilderWildcard = QueryBuilders.wildcardQuery(esCondition.getKey(), esCondition.getValue().toString());
                        bb = bb.filter(queryBuilderWildcard);
                        break;
                    case EsConstant.QUERY_TYPE_FUZZY://模糊匹配
                        QueryBuilder queryBuilderFuzzy = QueryBuilders.fuzzyQuery(esCondition.getKey(), esCondition.getValue());
                        bb = bb.filter(queryBuilderFuzzy);
                        //searchRequestBuilder.setQuery(queryBuilderIs);
                        break;
                    case EsConstant.QUERY_TYPE_REGEXP://正则匹配
                        QueryBuilder queryBuilderRegexp = QueryBuilders.regexpQuery(esCondition.getKey(), esCondition.getValue().toString());
                        bb = bb.filter(queryBuilderRegexp);
                        //searchRequestBuilder.setQuery(queryBuilderIs);
                        break;
                    case EsConstant.QUERY_TYPE_LT://小于
                        QueryBuilder queryBuilderLt = QueryBuilders.rangeQuery(esCondition.getKey()).lt(esCondition.getValue());
                        bb = bb.filter(queryBuilderLt);
                        //searchRequestBuilder.setQuery(queryBuilderLt);
                        break;
                    case EsConstant.QUERY_TYPE_GT://大于
                        QueryBuilder queryBuilderGt = QueryBuilders.rangeQuery(esCondition.getKey()).gt(esCondition.getValue());
                        bb = bb.filter(queryBuilderGt);
                        break;
                    case EsConstant.QUERY_TYPE_RANGE://范围查询
                        RangeQueryBuilder to = QueryBuilders.rangeQuery(esCondition.getKey()).from(esCondition.getValue()).to(esCondition.getValue2());
                        bb = bb.filter(to);
                        break;
                }
            }
            srb.setQuery(bb);
        }
        if (esSearchEntity.getEsPage() != null) {
            srb.setFrom(esSearchEntity.getEsPage().getFrom()).setSize(esSearchEntity.getEsPage().getPageSize());
        }
        if (esSearchEntity.getEsSort() != null) {
            srb.addSort(esSearchEntity.getEsSort().getSortKey(), esSearchEntity.getEsSort().isSortDesc() ? SortOrder.DESC : SortOrder.ASC);
        }
        return srb;
    }

    /**
     * 查询list
     * @param esSearchEntity
     * @return
     */
    public EsSearchResult searchList(EsSearchEntity esSearchEntity) {
        EsSearchResult esSearchResult =new EsSearchResult();
        SearchResponse searchResponse = searchResponse(esSearchEntity);
        EsPage esPage = esSearchEntity.getEsPage()==null?new EsPage(1,10):esSearchEntity.getEsPage();
        long totalHits = searchResponse.getHits().getTotalHits();
        esPage.setTotalRecord(totalHits);
        esSearchResult.setEsPage(esPage);
        List<Map<String,Object>> list=new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            list.add(hit.getSourceAsMap());
        }
        esSearchResult.setList(list);
        return esSearchResult;
    }

    /**
     * 查询第一个(结合排序使用)
     * @param esSearchEntity
     * @return
     */
    public Map<String,Object> searchFirst(EsSearchEntity esSearchEntity) {
        SearchResponse searchResponse = searchResponse(esSearchEntity);
        SearchHit[] hits = searchResponse.getHits().getHits();
        Map<String,Object> map =null;
        if(hits.length>0){
            map=hits[0].getSourceAsMap();
        }
        return map;
    }

    /**
     * 聚合查询
     * @param esAggregationEntity
     * @return
     */
    public SearchResponse searchAggregation(EsAggregationEntity esAggregationEntity) {
        EsSearchEntity esSearchEntity= esAggregationEntity.getEsSearchEntity();
        esSearchEntity.index(esAggregationEntity.getIndex());
        esSearchEntity.type(esAggregationEntity.getType());
        SearchRequestBuilder srb = getSearchRequestBuilder(esSearchEntity);
        if(srb==null){
            return null;
        }
        List<EsAggregation> esAggregations=esAggregationEntity.getEsAggregations();
        //寻找主分组
        EsAggregation mainAggregation =null;
        for (EsAggregation esAggregation : esAggregations) {
            if(esAggregation.getAggregationType().equals(EsConstant.AGGREGATION_TYPE_GROUPBY)&&
                    esAggregation.getIndex()==0){
                mainAggregation=esAggregation;
            }
        }
        if(mainAggregation==null){
            log.error("分组条件不能为空！！！");
            return null;
        }
        esAggregations.remove(mainAggregation);
        TermsAggregationBuilder mainAgg= AggregationBuilders.terms(mainAggregation.getAlias()).field(mainAggregation.getKey());
        for (EsAggregation esAggregation : esAggregations) {
            switch (esAggregation.getAggregationType()){
                case EsConstant.AGGREGATION_TYPE_GROUPBY:
                    TermsAggregationBuilder groupAgg= AggregationBuilders.terms(esAggregation.getAlias()).field(esAggregation.getKey());
                    mainAgg.subAggregation(groupAgg);
                    break;
                case EsConstant.AGGREGATION_TYPE_AVG:
                    AvgAggregationBuilder avgAggregationBuilder=AggregationBuilders.avg(esAggregation.getAlias()).field(esAggregation.getKey());
                    mainAgg.subAggregation(avgAggregationBuilder);
                    break;
                case EsConstant.AGGREGATION_TYPE_MAX:
                    MaxAggregationBuilder maxAggregationBuilder= AggregationBuilders.max(esAggregation.getAlias()).field(esAggregation.getKey());
                    mainAgg.subAggregation(maxAggregationBuilder);
                    break;
                case EsConstant.AGGREGATION_TYPE_MIN:
                    MinAggregationBuilder minAggregationBuilder=AggregationBuilders.min(esAggregation.getAlias()).field(esAggregation.getKey());
                    mainAgg.subAggregation(minAggregationBuilder);
                    break;
                case EsConstant.AGGREGATION_TYPE_SUM:
                    SumAggregationBuilder sumAggregationBuilder=AggregationBuilders.sum(esAggregation.getAlias()).field(esAggregation.getKey());
                    mainAgg.subAggregation(sumAggregationBuilder);
                    break;
            }
        }
        //获取分组后的排序
        for (EsAggregation esAggregation : esAggregations) {
            if(esAggregation.isSort()){
                mainAgg.order(Terms.Order.aggregation(esAggregation.getAlias(),esAggregation.isDesc()?false:true));
            }
        }
        mainAgg.size(esAggregationEntity.getSize());
        srb.addAggregation(mainAgg);
        SearchResponse response = srb.execute().actionGet();
        return response;
    }


    /**
     * 聚合查询数据
     * @param esAggregationEntity
     * @return
     */
    public List<Map<String,Object>> searchAggregationList(EsAggregationEntity esAggregationEntity) {
        List<Map<String,Object>> list =new ArrayList<>();
        EsAggregation mainAggregation =null;
        for (EsAggregation esAggregation : esAggregationEntity.getEsAggregations()) {
            if(esAggregation.getAggregationType().equals(EsConstant.AGGREGATION_TYPE_GROUPBY)&&
                    esAggregation.getIndex()==0){
                mainAggregation=esAggregation;
            }
        }
        if(mainAggregation==null){
            log.error("分组条件不能为空！！！");
            return null;
        }
        SearchResponse response=searchAggregation(esAggregationEntity);
        Map<String, Aggregation> aggMap=response.getAggregations().asMap();
        StringTerms teamAgg= (StringTerms) aggMap.get(mainAggregation.getAlias());
        Iterator<StringTerms.Bucket> teamBucketIt =teamAgg.getBuckets().iterator();
        while (teamBucketIt .hasNext()) {
            Map<String,Object> map =new HashMap<>();
            StringTerms.Bucket buck = teamBucketIt.next();
            //主分组值
            String team = (String)buck.getKey();
            //记录数
            long count = buck.getDocCount();
            map.put(mainAggregation.getKey(),team);
            map.put(mainAggregation.getAggregationType(),count);
            //得到所有子聚合
            Map subaggmap = buck.getAggregations().asMap();
            for (EsAggregation esAggregation : esAggregationEntity.getEsAggregations()) {
                if(esAggregation.getAggregationType().equals(EsConstant.AGGREGATION_TYPE_GROUPBY)&&
                        esAggregation.getIndex()==0){
                }else{
                    Object o = subaggmap.get(esAggregation.getAlias());
                    switch (esAggregation.getAggregationType()){
                        case EsConstant.AGGREGATION_TYPE_GROUPBY:
                            break;
                        case EsConstant.AGGREGATION_TYPE_AVG:
                            map.put(esAggregation.getAlias(),((InternalAvg)o).getValue());
                            break;
                        case EsConstant.AGGREGATION_TYPE_MAX:
                            map.put(esAggregation.getAlias(),((InternalMax)o).getValue());
                            break;
                        case EsConstant.AGGREGATION_TYPE_MIN:
                            map.put(esAggregation.getAlias(),((InternalMin)o).getValue());
                            break;
                        case EsConstant.AGGREGATION_TYPE_SUM:
                            map.put(esAggregation.getAlias(),((InternalSum)o).getValue());
                            break;
                    }
                }
            }
            list.add(map);
        }
        return list;
    }
}
