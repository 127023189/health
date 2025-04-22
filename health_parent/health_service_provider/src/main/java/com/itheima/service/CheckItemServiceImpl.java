package com.itheima.service;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 检查项服务
 */
@org.springframework.stereotype.Service
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;
    //新增
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }


    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckItem> page = checkItemDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }


    public void delete(Integer id) {
        // 检查是否与检查组有关
        long count = checkItemDao.findCountByCheckItemId(id);
        if(count > 0){
            throw new RuntimeException("当前检查项被引用，不能删除");
        }else{
            checkItemDao.deleteById(id);
        }
    }


    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }


    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }


    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }


}