package com.my.mongo.entity;

import cn.hutool.json.JSONObject;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Getter
public class MongoOperationEntity {
    /**
     * 集合名称
     */
    private String collectionName;
    /**
     * 分页实体
     */
    private Page page;
    /**
     * 查询list
     */
    private List<MongoCondition> mongoConditions;

    /**
     * 类型
     */
    private Class clazz=HashMap.class;
    /**
     * 数据
     */
    private Object obj;
    /**
     * 更新数据
     */
    private JSONObject updateDate;
    /**
     * 排序
     */
    private Sort sort;

    public MongoOperationEntity sort(boolean idDesc, String... keys){
        this.sort=new Sort(idDesc?Sort.Direction.DESC:Sort.Direction.ASC,keys);
        return this;
    }

    /**
     * 更新数据
     * @param updateDate
     * @return
     */
    public MongoOperationEntity updateDate(JSONObject updateDate){
        this.updateDate=updateDate;
        return this;
    }

    /**
     * 设置数据对象
     * @param obj
     * @return
     */
    public MongoOperationEntity obj(Object obj){
        this.obj=obj;
        return this;
    }

    /**
     * 数据类型
     * @param clazz
     * @return
     */
    public MongoOperationEntity clazz(Class clazz){
        this.clazz=clazz;
        return this;
    }

    /**
     * 添加mongo集合名称
     * @param collectionName
     * @return
     */
    public MongoOperationEntity collectionName(String collectionName){
        this.collectionName=collectionName;
        return this;
    }

    /**
     * 设置分页对象
     * @param page
     * @param size
     * @return
     */
    public MongoOperationEntity Page(int page, int size){
        this.page=new Page(page,size);
        return this;
    }


    /**
     * 添加条件
     * @param key 列
     * @param queryType 查询类型
     * @param value  值
     * @param value1 值1
     * @return
     */
    public MongoOperationEntity addCondition(String key, String queryType, Object value, Object value1) {
        List<MongoCondition> mongoConditions = this.getMongoConditions() == null ? new ArrayList<MongoCondition>() : this.getMongoConditions();
        mongoConditions.add(new MongoCondition(key,queryType,value,value1));
        this.mongoConditions=mongoConditions;
        return this;
    }

}
