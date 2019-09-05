package com.my.es.entity;

import lombok.Getter;


@Getter
public class EsSort {
    private String sortKey;
    /**
     * 是否降序排列
     */
    private boolean sortDesc=true;

    public EsSort(String sortKey, boolean sortDesc) {
        this.sortKey = sortKey;
        this.sortDesc = sortDesc;
    }
}
