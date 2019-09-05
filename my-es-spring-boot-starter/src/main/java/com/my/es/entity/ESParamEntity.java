package com.my.es.entity;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class ESParamEntity {
    /**
     * 是否分页
     */
    private boolean isPage;
    /**
     * 查询条件
     */
    private Map<String,Object> queryMap;
    /**
     * 查询条件list
     */
    private List<Map<String,Object>> queryList;
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
    private String esId;
    /**
     * 数据对象
     */
    private JSONObject jsonObj;
    /**
     * 第几页
     */
    private int form;
    /**
     * 每页大小
     */
    private int size;
    /**
     * 排序字段
     */
    private String sort;
    /**
     * 是否降序排列
     */
    private boolean sortDesc;

    public void setJsonObj(Object obj) {
        this.jsonObj = JSONUtil.parseObj(obj);
    }
}
