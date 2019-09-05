package com.my.mongo.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.my.mongo.constants.IotMongoPluginConstants;
import com.my.mongo.entity.MongoCondition;
import com.my.mongo.entity.MongoOperationEntity;
import com.my.mongo.entity.Page;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Data
@Slf4j
@Component
public class IotMongoTemplate {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 添加数据(支持批量操作)
     * @param mongoOperationEntity
     */
    public void addMongoDate(MongoOperationEntity mongoOperationEntity){
        Object obj = mongoOperationEntity.getObj();
        if(obj instanceof ArrayList){
            List<Object> objects = (ArrayList)obj;
            if(mongoOperationEntity.getCollectionName()!=null){
                mongoTemplate.insert(objects, mongoOperationEntity.getCollectionName());
            }else{
                mongoTemplate.insert(objects);
            }
        }else{
            if(mongoOperationEntity.getCollectionName()!=null){
                mongoTemplate.insert(obj, mongoOperationEntity.getCollectionName());
            }else{
                mongoTemplate.insert(obj);
            }
        }


    }

    /**
     * 删除mongo数据
     * @param mongoOperationEntity
     */
    public void delMongoDateByQuery(MongoOperationEntity mongoOperationEntity){
//        Query query = new Query();
//        Criteria selCriteria = getSelCriteria(mongoOperationEntity);
//        query.addCriteria(selCriteria);
        DBObject selCriteria = getSelCriteria(mongoOperationEntity);
        if(mongoOperationEntity.getCollectionName()!=null){
            mongoTemplate.getCollection(mongoOperationEntity.getCollectionName()).remove(selCriteria);
            //mongoTemplate.remove(query,mongoOperationEntity.getCollectionName());
        }else{
            mongoTemplate.getCollection(mongoTemplate.getCollectionName(mongoOperationEntity.getClazz())).remove(selCriteria);
            //mongoTemplate.remove(query,mongoOperationEntity.getClazz());
        }
    }

    /**
     * 更新mongo数据
     * @param mongoOperationEntity
     * @return
     */
    public Object updateMongoDateByQuery(MongoOperationEntity mongoOperationEntity){
//        Query query = new Query();
        //Criteria selCriteria = getSelCriteria(mongoOperationEntity);
        //query.addCriteria(selCriteria);
//        Update update =new Update();
//        JSONObject updateDate = mongoOperationEntity.getUpdateDate();
//        Set<String> strings = updateDate.keySet();
//        for (String key : strings) {
//            update.set(key,updateDate.get(key));
//        }
        DBObject selCriteria = getSelCriteria(mongoOperationEntity);
        DBObject update =new BasicDBObject();
        JSONObject updateDate = mongoOperationEntity.getUpdateDate();
        Set<String> strings = updateDate.keySet();
        for (String key : strings) {
            update.put(key,updateDate.get(key));
        }
        if(mongoOperationEntity.getCollectionName()!=null){
            return mongoTemplate.getCollection(mongoOperationEntity.getCollectionName()).update(selCriteria,update);
            //return mongoTemplate.updateMulti(query,update,mongoOperationEntity.getClazz(), mongoOperationEntity.getCollectionName());
        }else{
            return mongoTemplate.getCollection(mongoTemplate.getCollectionName(mongoOperationEntity.getClazz())).update(selCriteria,update);
            //return mongoTemplate.updateMulti(query,update, mongoOperationEntity.getClazz());
        }
    }

    /**
     * 更新mongo一条数据
     * @param mongoOperationEntity
     * @return
     */
    @Deprecated
    public WriteResult updateOneMongoDate(MongoOperationEntity mongoOperationEntity){
        Query query = new Query();
        //Criteria selCriteria = getSelCriteria(mongoOperationEntity);
        //query.addCriteria(selCriteria);
        Update update =new Update();
        JSONObject updateDate = mongoOperationEntity.getUpdateDate();
        Set<String> strings = updateDate.keySet();
        for (String key : strings) {
            update.set(key,updateDate.get(key));
        }
        if(mongoOperationEntity.getCollectionName()!=null){
            return mongoTemplate.updateFirst(query,update, mongoOperationEntity.getCollectionName());
        }else{
            return mongoTemplate.updateFirst(query,update, mongoOperationEntity.getClazz());
        }
    }


    /**
     * 分页查询数据
     * @param mongoOperationEntity
     * @return
     */
    public Page findListByPage(MongoOperationEntity mongoOperationEntity) {
        int pageNum= mongoOperationEntity.getPage().getPageNumber()==0 ? IotMongoPluginConstants.DEFAULT_PAGE_NUM: mongoOperationEntity.getPage().getPageNumber();
        int pageSize= mongoOperationEntity.getPage().getPageSize()==0?IotMongoPluginConstants.DEFAULT_PAGE_SIZE : mongoOperationEntity.getPage().getPageSize();
        Query query = new Query();
        //查询总数
        long totalCount =0L;
        //query.addCriteria(getSelCriteria(mongoOperationEntity));

        DBObject selCriteria = getSelCriteria(mongoOperationEntity);

        if(mongoOperationEntity.getCollectionName()!=null){
            //totalCount = mongoTemplate.getCollection(mongoOperationEntity.getCollectionName()).count(selCriteria);
            //totalCount = mongoTemplate.count(query, mongoOperationEntity.getCollectionName());
        }else{
            //totalCount = mongoTemplate.getCollection(mongoTemplate.getCollectionName(mongoOperationEntity.getClazz())).count(selCriteria);
            //totalCount = mongoTemplate.count(query, mongoOperationEntity.getClazz());
        }
        query.skip((pageNum - 1) * pageSize).limit(pageSize);
        //排序
        if (mongoOperationEntity.getSort()!=null){
            query.with(mongoOperationEntity.getSort());
        }
        List list =new ArrayList();
        if(mongoOperationEntity.getCollectionName()!=null){
            DBCursor dbObjects = mongoTemplate.getCollection(mongoOperationEntity.getCollectionName()).find(selCriteria);
            while (dbObjects.hasNext()){
                DBObject object=dbObjects.next();
                list.add(object);
            }
            //list = mongoTemplate.find(query, mongoOperationEntity.getClazz(), mongoOperationEntity.getCollectionName());
        }else{
//            list = mongoTemplate.find(query, mongoOperationEntity.getClazz());
            DBCursor dbObjects = mongoTemplate.getCollection(mongoTemplate.getCollectionName(mongoOperationEntity.getClazz())).find(selCriteria);
            while (dbObjects.hasNext()){
                DBObject object=dbObjects.next();
                list.add(object);
            }
        }
        Page page = new Page(pageNum,pageSize);
        page.setListData(list);
        page.setTotalCount(totalCount);
        return page;

    }

    /**
     * 查询list数据
     * @param mongoOperationEntity
     * @return
     */
    public List findList(MongoOperationEntity mongoOperationEntity) {
        Query query = new Query();
        //排序
        if (mongoOperationEntity.getSort()!=null){
            query.with(mongoOperationEntity.getSort());
        }
        //query.addCriteria(getSelCriteria(mongoOperationEntity));
        DBObject selCriteria = getSelCriteria(mongoOperationEntity);
        List list =new ArrayList();
        if(mongoOperationEntity.getCollectionName()!=null){
//            list = mongoTemplate.find(query, mongoOperationEntity.getClazz(), mongoOperationEntity.getCollectionName());
            DBCursor dbObjects = mongoTemplate.getCollection(mongoOperationEntity.getCollectionName()).find(selCriteria);
            while (dbObjects.hasNext()){
                DBObject object=dbObjects.next();
                list.add(object);
            }
        }else{
            DBCursor dbObjects = mongoTemplate.getCollection(mongoTemplate.getCollectionName(mongoOperationEntity.getClazz())).find(selCriteria);
            while (dbObjects.hasNext()){
                DBObject object=dbObjects.next();
                list.add(object);
            }
            //list = mongoTemplate.find(query, mongoOperationEntity.getClazz());
        }
        return list;

    }

    /**
     * 查询一条数据
     * @param mongoOperationEntity
     * @return
     */
    public JSONObject findOne(MongoOperationEntity mongoOperationEntity) {
        Query query = new Query();
        //query.addCriteria(getSelCriteria(mongoOperationEntity));
        DBObject selCriteria = getSelCriteria(mongoOperationEntity);
        if(mongoOperationEntity.getCollectionName()!=null){
            DBObject one = mongoTemplate.getCollection(mongoOperationEntity.getCollectionName()).findOne(selCriteria);
            return JSONUtil.parseObj(one);
            //return JSONUtil.parseObj(mongoTemplate.findOne(query, mongoOperationEntity.getClazz(), mongoOperationEntity.getCollectionName()));
        }else{
            //return JSONUtil.parseObj(mongoTemplate.findOne(query, mongoOperationEntity.getClazz()));
            DBObject one = mongoTemplate.getCollection(mongoTemplate.getCollectionName(mongoOperationEntity.getClazz())).findOne(selCriteria);
            return JSONUtil.parseObj(one);
        }
    }


    /**
     * 封装查询条件
     * @param mongoOperationEntity
     * @return
     */
    private DBObject getSelCriteria(MongoOperationEntity mongoOperationEntity){
        //setup the query criteria 设置查询条件
        DBObject query = new BasicDBObject();
        Criteria criteria = Criteria.where("_id").exists(true);
        List<MongoCondition> mongoConditions = mongoOperationEntity.getMongoConditions();
        for (MongoCondition mongoCondition : mongoConditions) {
            //判断查询类型
            switch (mongoCondition.getQueryType()) {
                case IotMongoPluginConstants.QUERY_TYPE_IS://精确匹配
                    query.put(mongoCondition.getKey(), new BasicDBObject("$eq", mongoCondition.getValue()));
                    //criteria.and(mongoCondition.getKey()).is(mongoCondition.getValue());
                    break;
                case IotMongoPluginConstants.QUERY_TYPE_GT://大于
                    query.put(mongoCondition.getKey(), new BasicDBObject("$gt", mongoCondition.getValue()));
                    //criteria.and(mongoCondition.getKey()).gt(mongoCondition.getValue());
                    break;
                case IotMongoPluginConstants.QUERY_TYPE_LT://小于
                    query.put(mongoCondition.getKey(), new BasicDBObject("$lt", mongoCondition.getValue()));
                    //criteria.and(mongoCondition.getKey()).lt(mongoCondition.getValue());
                    break;
                case IotMongoPluginConstants.QUERY_TYPE_GTE://大于
                    query.put(mongoCondition.getKey(), new BasicDBObject("$gte", mongoCondition.getValue()));
                    //criteria.and(mongoCondition.getKey()).gte(mongoCondition.getValue());
                    break;
                case IotMongoPluginConstants.QUERY_TYPE_LTE://小于
                    query.put(mongoCondition.getKey(), new BasicDBObject("$lte", mongoCondition.getValue()));
                    //criteria.and(mongoCondition.getKey()).lte(mongoCondition.getValue());
                    break;
                case IotMongoPluginConstants.QUERY_TYPE_RANGE://范围查询
                    query.put(mongoCondition.getKey(), (new BasicDBObject("$gte", mongoCondition.getValue())).append("$lte", mongoCondition.getValue2()));
                    //criteria.and(mongoCondition.getKey()).gte(mongoCondition.getValue()).lte(mongoCondition.getValue2());
                    break;
                case IotMongoPluginConstants.QUERY_TYPE_IN://子查询
                    query.put(mongoCondition.getKey(), new BasicDBObject("$in", mongoCondition.getValue()));
                    //criteria.and(mongoCondition.getKey()).in(mongoCondition.getValue());
                    break;
                case IotMongoPluginConstants.QUERY_TYPE_EXISTS://是否存在
                    criteria.exists(Boolean.parseBoolean(mongoCondition.getValue().toString()));
                    break;
            }
        }
        return query;
    }
}
