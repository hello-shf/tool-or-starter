package com.my.es.entity;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Getter;

import java.util.UUID;


@Getter
public class EsOtherEntity {
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
    private String esId= UUID.randomUUID().toString();
    /**
     * 数据对象
     */
    private JSONObject jsonObj;
    /**
     * 数据对象数组
     */
    private JSONArray jsonObjList;
    /**
     * 查询实体
     */
    private EsSearchEntity esSearchEntity;
    /**
     * 设置index
     * @param index
     * @return
     */
    public EsOtherEntity index(String index) {
        this.index=index;
        return this;
    }

    /**
     * 设置type
     * @param type
     * @return
     */
    public EsOtherEntity type(String type) {
        this.type=type;
        return this;
    }

    /**
     * 设置id
     * @param esId
     * @return
     */
    public EsOtherEntity esId(String esId) {
        this.esId = esId;
        return this;
    }

    /**
     * 设置对象
     * @param obj
     * @return
     */
    public EsOtherEntity jsonObj(Object obj) {
        this.jsonObj = JSONUtil.parseObj(obj);
        return this;
    }

    /**
     * 批量操作对象
     * @param array
     * @return
     */
    public EsOtherEntity jsonObjList(Object array) {
        this.jsonObjList = JSONUtil.parseArray(array);
        return this;
    }

    public EsOtherEntity setEsSearchEntity(EsSearchEntity esSearchEntity) {
        this.esSearchEntity = esSearchEntity;
        return this;
    }
}
