package com.my.es.entity;

import com.my.es.constant.EsConstant;
import lombok.Getter;


@Getter
public class EsCondition {
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
    private String queryType= EsConstant.QUERY_TYPE_IS;

    protected EsCondition(String key,String queryType,Object value,Object value2){
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
    private EsCondition is(String key,String value) {
        this.queryType= EsConstant.QUERY_TYPE_IS;
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
    private EsCondition range(String key,String rangeBegin,String rangeEnd) {
        this.queryType = EsConstant.QUERY_TYPE_RANGE;
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
    private EsCondition gt(String key,String value) {
        this.queryType = EsConstant.QUERY_TYPE_GT;
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
    private EsCondition lt(String key,String value) {
        this.queryType = EsConstant.QUERY_TYPE_LT;
        this.key = key;
        this.value = value;
        return this;
    }

}
