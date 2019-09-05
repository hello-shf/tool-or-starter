package com.my.es.entity;

import lombok.Getter;


@Getter
public class EsAggregation {
    /**
     * key
     */
    private String key;
    /**
     * 聚合方式
     */
    private String aggregationType;
    /**
     * 别名
     */
    private String alias;
    /**
     * 排序
     */
    private int index=0;
    /**
     * 是否排序
     */
    private boolean isSort=false;
    /**
     * 是否降序排列
     */
    private boolean isDesc=true;

    protected EsAggregation(String key, String aggregationType,String alias,int index) {
        this.key = key;
        this.aggregationType = aggregationType;
        this.alias=alias;
        this.index=index;
    }
    protected EsAggregation(String key, String aggregationType,String alias) {
        this.key = key;
        this.aggregationType = aggregationType;
        this.alias=alias;
    }

    protected EsAggregation(String key, String aggregationType, String alias, boolean isSort, boolean isDesc) {
        this.key = key;
        this.aggregationType = aggregationType;
        this.alias = alias;
        this.isSort = isSort;
        this.isDesc = isDesc;
    }

    protected EsAggregation(String key, String aggregationType, String alias, boolean isSort) {
        this.key = key;
        this.aggregationType = aggregationType;
        this.alias = alias;
        this.isSort = isSort;
    }

    protected EsAggregation(String key, String aggregationType, String alias, int index, boolean isSort) {
        this.key = key;
        this.aggregationType = aggregationType;
        this.alias = alias;
        this.index = index;
        this.isSort = isSort;
    }

    protected EsAggregation(String key, String aggregationType, String alias, int index, boolean isSort, boolean isDesc) {
        this.key = key;
        this.aggregationType = aggregationType;
        this.alias = alias;
        this.index = index;
        this.isSort = isSort;
        this.isDesc = isDesc;
    }
}
