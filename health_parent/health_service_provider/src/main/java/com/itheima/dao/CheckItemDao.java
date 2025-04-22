package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CheckItemDao {
    public void add(CheckItem checkItem);

    Page<CheckItem> selectByCondition(String queryString);

    void deleteById(Integer id);

    long findCountByCheckItemId(Integer id);

    CheckItem findById(Integer id);

    void edit(CheckItem checkItem);

    List<CheckItem> findAll();
}
