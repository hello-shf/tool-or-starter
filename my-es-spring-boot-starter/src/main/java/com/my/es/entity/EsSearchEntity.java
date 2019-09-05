package com.my.es.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class EsSearchEntity {
    /**
     * 索引
     */
    private  String index;
    /**
     * 类型
     */
    private String type;
    /**
     *id
     */
    private String esId =UUID.randomUUID().toString();
    /**
     * 条件实体
     */
    private List<EsCondition> esConditionList;
    /**
     * 分页实体
     */
    private EsPage esPage;

    /**
     * 排序实体
     */
    private EsSort esSort;

    /**
     * 设置index
     * @param index
     * @return
     */
    public EsSearchEntity index(String index) {
        this.index=index;
        return this;
    }

    /**
     * 设置type
     * @param type
     * @return
     */
    public EsSearchEntity type(String type) {
        this.type=type;
        return this;
    }

    /**
     * 设置id
     * @param esId
     * @return
     */
    public EsSearchEntity esId(String esId) {
        this.esId = esId;
        return this;
    }

    /**
     * 添加查询实体
     * @param key
     * @param queryType
     * @param value
     * @param value1
     * @return
     */
    public EsSearchEntity addCondition(String key,String queryType,Object value,Object value1) {
        List<EsCondition> esConditions = this.getEsConditionList() == null ? new ArrayList<EsCondition>() : this.getEsConditionList();
        esConditions.add(new EsCondition(key,queryType,value,value1));
        this.esConditionList=esConditions;
        return this;
    }

    /**
     * 封装分页实体
     * @param pageNum 第几页 默认1
     * @param pageSize 每页总数 默认10
     * @return
     */
    public EsSearchEntity esPage(int pageNum,int pageSize) {
        this.esPage=new EsPage(pageNum,pageSize);
        return this;
    }

    /**
     * 设置排序字段
     * @param sortKey
     * @param sortDesc
     * @return
     */
    public EsSearchEntity esSort(String sortKey, boolean sortDesc) {
        this.esSort=new EsSort(sortKey,sortDesc);
        return this;
    }
}
