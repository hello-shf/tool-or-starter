package com.my.datasource.core.vm;


import java.io.Serializable;


public class SmartSort implements Serializable {

    /**
     *排序字段
     */
    private String predicate;

    /**
     * 是否升序
     */
    private boolean reverse;

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public boolean getReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
}
