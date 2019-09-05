package com.my.es.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class EsAggregationEntity {
    /**
     * 索引
     */
    private  String index;
    /**
     * 类型
     */
    private String type;
    /**
     * 聚合条件
     */
    List<EsAggregation> esAggregations;
    /**
     * 查询实体
     */
    private EsSearchEntity esSearchEntity;
    /**
     * 长度
     */
    private int size=10;

    /**
     * 设置index
     * @param index
     * @return
     */
    public EsAggregationEntity index(String index) {
        this.index=index;
        return this;
    }

    /**
     * 设置type
     * @param type
     * @return
     */
    public EsAggregationEntity type(String type) {
        this.type=type;
        return this;
    }

    /**
     * 设置size
     * @param size
     * @return
     */
    public EsAggregationEntity size(int size) {
        this.size=size;
        return this;
    }

    /**
     * 添加聚合查询
     * @param key 列名
     * @param aggregationType 查询类型
     * @param alias 别名
     * @return
     */
    public EsAggregationEntity addEsAggregations(String key, String aggregationType,String alias) {
        EsAggregation esAggregation =new EsAggregation(key,aggregationType,alias);
        if(this.esAggregations==null){
            this.esAggregations=new ArrayList<>();
        }
        this.esAggregations.add(esAggregation);
        return this;
    }

    /**
     *
     * @param key
     * @param aggregationType
     * @param alias
     * @param index
     * @return
     */
    public EsAggregationEntity addEsAggregations(String key, String aggregationType,String alias,int index) {
        EsAggregation esAggregation =new EsAggregation(key,aggregationType,alias,index);
        if(this.esAggregations==null){
            this.esAggregations=new ArrayList<>();
        }
        this.esAggregations.add(esAggregation);
        return this;
    }

    /**
     *
     * @param key
     * @param aggregationType
     * @param alias
     * @param isSort
     * @param isDesc
     * @return
     */
    public EsAggregationEntity addEsAggregations(String key, String aggregationType,String alias,boolean isSort, boolean isDesc) {
        EsAggregation esAggregation =new EsAggregation(key,aggregationType,alias,isSort,isDesc);
        if(this.esAggregations==null){
            this.esAggregations=new ArrayList<>();
        }
        this.esAggregations.add(esAggregation);
        return this;
    }

    /**
     *
     * @param key
     * @param aggregationType
     * @param alias
     * @param index
     * @param isSort
     * @param isDesc
     * @return
     */
    public EsAggregationEntity addEsAggregations(String key, String aggregationType,String alias,int index,boolean isSort, boolean isDesc) {
        EsAggregation esAggregation =new EsAggregation(key,aggregationType,alias,index,isSort,isDesc);
        if(this.esAggregations==null){
            this.esAggregations=new ArrayList<>();
        }
        this.esAggregations.add(esAggregation);
        return this;
    }

    /**
     *
     * @param key
     * @param aggregationType
     * @param alias
     * @param isSort
     * @return
     */
    public EsAggregationEntity addEsAggregations(String key, String aggregationType,String alias,boolean isSort) {
        EsAggregation esAggregation =new EsAggregation(key,aggregationType,alias,isSort);
        if(this.esAggregations==null){
            this.esAggregations=new ArrayList<>();
        }
        this.esAggregations.add(esAggregation);
        return this;
    }

    /**
     *
     * @param key
     * @param aggregationType
     * @param alias
     * @param index
     * @param isSort
     * @return
     */
    public EsAggregationEntity addEsAggregations(String key, String aggregationType,String alias,int index,boolean isSort) {
        EsAggregation esAggregation =new EsAggregation(key,aggregationType,alias,index,isSort);
        if(this.esAggregations==null){
            this.esAggregations=new ArrayList<>();
        }
        this.esAggregations.add(esAggregation);
        return this;
    }

    /**
     *
     * @param esSearchEntity
     * @return
     */
    public EsAggregationEntity setEsSearchEntity(EsSearchEntity esSearchEntity) {
        this.esSearchEntity = esSearchEntity;
        return this;
    }
}
