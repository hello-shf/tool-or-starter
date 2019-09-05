package com.my.es.constant;


public class EsConstant {
    /**
     *精确匹配
     */
    public static final String QUERY_TYPE_IS= "is";
    /**
     *存在查询
     */
    public static final String QUERY_TYPE_EXISTS= "exists";
    /**
     *不存在查询
     */
    public static final String QUERY_TYPE_NOTEXISTS= "notexists";
    /**
     *范围查询开始
     */
    public static final String QUERY_TYPE_RANGE= "range";
    /**
     *范围查询开始
     */
    public static final String QUERY_TYPE_RANGE_BEGIN= "rangeBegin";
    /**
     *范围查询结束
     */
    public static final String QUERY_TYPE_RANGE_END= "rangeEnd";
    /**
     * 前缀查询
     */
    public static final String QUERY_TYPE_PREFIX= "prefix";
    /**
     * 模糊查询
     */
    public static final String QUERY_TYPE_FUZZY= "fuzzy";
    /**
     * 正则查询
     */
    public static final String QUERY_TYPE_REGEXP= "regexp";
    /**
     * 通配符查询查询
     */
    public static final String QUERY_TYPE_WILDCARD= "wildcard";
    /**
     * 模糊查询gt
     */
    public static final String QUERY_TYPE_GT= "gt";
    /**
     * 模糊查询lt
     */
    public static final String QUERY_TYPE_LT= "lt";

    /**
     * 分组
     */
    public static final String AGGREGATION_TYPE_GROUPBY= "groupby";
    /**
     * 最大值
     */
    public static final String AGGREGATION_TYPE_MAX= "max";
    /**
     * 最小值
     */
    public static final String AGGREGATION_TYPE_MIN= "min";
    /**
     * 求和
     */
    public static final String AGGREGATION_TYPE_SUM= "sum";
    /**
     * 平均
     */
    public static final String AGGREGATION_TYPE_AVG= "avg";


}
