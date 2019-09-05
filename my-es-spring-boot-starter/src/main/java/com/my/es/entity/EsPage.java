package com.my.es.entity;

import lombok.Data;
import lombok.Getter;


@Getter
public class EsPage {
    //当前页,从请求那边传过来。
    private int pageNum=1;
    //每页显示的数据条数。
    private int pageSize=10;
    //总的记录条数。查询数据库得到的数据
    private long totalRecord;
    private long totalPage;
    private int from;

    public EsPage(int pageNum,int pageSize){
        this.pageNum=pageNum;
        this.pageSize=pageSize;
        this.from=(pageNum-1)*pageSize;
    }

    /**
     * 设置总条数
     * @param totalRecord
     */
    public void setTotalRecord(long totalRecord) {
        this.totalRecord = totalRecord;
        this.totalPage=totalRecord%pageSize==0?totalRecord/pageSize:totalRecord/pageSize+1;
    }
}
