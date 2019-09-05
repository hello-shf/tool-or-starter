package com.my.datasource.core.vm;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.Serializable;


public class SmartPageVM<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分页相关属性
     */
    private Page page;

    /**
     * 查询参数
     */
    private T search;

    /**
     * 排序参数
     */
    private SmartSort sort;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public T getSearch() {
        return search;
    }

    public void setSearch(T search) {
        this.search = search;
    }

    public SmartSort getSort() {
        return sort;
    }

    public void setSort(SmartSort sort) {
        this.sort = sort;
    }
}
