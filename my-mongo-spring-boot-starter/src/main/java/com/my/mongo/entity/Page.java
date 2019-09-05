package com.my.mongo.entity;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;


@Data
public class Page extends PageRequest {
    public Page(int page, int size) {
        super(page, size);
    }
    public Page(int page, int size, Sort sort) {
        super(page, size, sort);
    }
    private List<?> listData;//实体封装数据
    // 数据
    private List<Map<String, Object>> results;
    // 总页数
    private Long pageCount = 0L;
    // 总条数
    private Long totalCount = 0L;

    public void setTotalCount(Long totalCount) {
        if(totalCount%getPageSize()==0){
            this.pageCount=totalCount/getPageSize();
        }else{
            this.pageCount=totalCount/getPageSize()+1;
        }
        this.totalCount = totalCount;
    }
}
