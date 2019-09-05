package com.my.mongo.constants;


public class IotMongoPluginConstants {
    //成功
    public static final  String SUCCESS = "success";
    //失败
    public static final  String FAIL = "fail";
    //默认分页页数
    public static final  int DEFAULT_PAGE_NUM = 1;
    //默认分页数量
    public static final  int DEFAULT_PAGE_SIZE = 20;

    /**
     *精确匹配
     */
    public static final String QUERY_TYPE_IS= "is";
    /**
     *范围查询开始
     */
    public static final String QUERY_TYPE_RANGE= "range";
    /**
     * 模糊查询
     */
    public static final String QUERY_TYPE_FUZZY= "fuzzy";
    /**
     * 模糊查询gt
     */
    public static final String QUERY_TYPE_GT= "gt";
    /**
     * 模糊查询lt
     */
    public static final String QUERY_TYPE_LT= "lt";
    /**
     * 模糊查询大于等于
     */
    public static final String QUERY_TYPE_GTE= "gte";
    /**
     * 模糊查询小于等于
     */
    public static final String QUERY_TYPE_LTE= "lte";

    /**
     * 子查询
     */
    public static final String QUERY_TYPE_IN= "IN";
    /**
     * 是否存在
     */
    public static final String QUERY_TYPE_EXISTS= "exists";

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