package com.itheima.entity;

import java.io.Serializable;

public class PageResult implements Serializable {
    private Long total; // 总记录数
    private Object rows; // 当前页结果

    public PageResult(Long total, Object rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Object getRows() {
        return rows;
    }

    public void setRows(Object rows) {
        this.rows = rows;
    }
}
