package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;

import java.util.List;


public interface CheckItemService {
    public void add(CheckItem checkItem);


    PageResult findPage(Integer currentPage, Integer pageSize, String queryString);

    void delete(Integer id);

    void edit(CheckItem checkItem);

    CheckItem findById(Integer id);

    List<CheckItem> findAll();
}
