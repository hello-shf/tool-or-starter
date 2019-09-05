package com.my.es.entity;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class EsSearchResult {
    /**
     * es分页实体
     */
    private EsPage esPage;
    /**
     * 返回结果
     */
    private List<Map<String,Object>> list;
}
