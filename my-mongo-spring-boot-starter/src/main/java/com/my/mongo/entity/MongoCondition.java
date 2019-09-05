package com.my.mongo.entity;

import com.my.mongo.constants.IotMongoPluginConstants;
import lombok.Getter;


@Getter
public class MongoCondition {
    /**
     * key
     */
    private String key ="***";
    /**
     * 值
     */
    private Object value=null;
    /**
     * 值2
     */
    private Object value2=null;
    /**
     * 查询条件
     */
    private String queryType=IotMongoPluginConstants.QUERY_TYPE_IS;

    protected MongoCondition(String key, String queryType, Object value, Object value2){
        this.key=key;
        this.queryType=queryType;
        this.value=value;
        this.value2=value2;
    }


    /**
     * 匹配查询
     * @param key 列名
     * @param value 列值
     * @return EsCondition条件实体
     */
    private MongoCondition is(String key,String value) {
        this.queryType= IotMongoPluginConstants.QUERY_TYPE_IS;
        this.key = key;
        this.value = value;
        return this;
    }

    /**
     * 范围查询
     * @param key 列名
     * @param rangeBegin 开始值
     * @param rangeEnd  结束值
     * @return
     */
    private MongoCondition range(String key,String rangeBegin,String rangeEnd) {
        this.queryType = IotMongoPluginConstants.QUERY_TYPE_RANGE;
        this.key = key;
        this.value = rangeBegin;
        this.value2 = rangeEnd;
        return this;
    }

    /**
     * 大于
     * @param key 列名
     * @param value 列值
     * @return
     */
    private MongoCondition gt(String key,String value) {
        this.queryType = IotMongoPluginConstants.QUERY_TYPE_GT;
        this.key = key;
        this.value = value;
        return this;
    }

    /**
     * 小于
     * @param key 列名
     * @param value 列值
     * @return
     */
    private MongoCondition lt(String key,String value) {
        this.queryType = IotMongoPluginConstants.QUERY_TYPE_LT;
        this.key = key;
        this.value = value;
        return this;
    }

}
