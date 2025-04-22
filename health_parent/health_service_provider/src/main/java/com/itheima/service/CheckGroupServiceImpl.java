package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService{

    @Autowired
    private CheckGroupDao checkGroupDao;

    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        checkGroupDao.add(checkGroup);

        // 关联检查项组
        setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);
    }


    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckItem> page = checkGroupDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }


    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }


    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }


    //编辑检查组，同时需要更新和检查项的关联关系
    public void eidt(CheckGroup checkGroup, Integer[] checkitemIds) {
        // 根据检查组id删除中间表数据（清理原有关联关系）
        checkGroupDao.deleteAssociation(checkGroup.getId());
        // 向中间表设置数据（检查项和检查组多对多关系）
        setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);

        // 设置检查组和检查项关联关系
        checkGroupDao.edit(checkGroup);
    }

    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    public void delete(Integer id) {
        // 删除中间表数据
        checkGroupDao.deleteAssociation(id);
        // 删除检查组数据
        checkGroupDao.deleteById(id);
    }

    public void setCheckGroupAndCheckItem(Integer checkGroupId,Integer[] checkitemIds){
        if(checkitemIds != null && checkitemIds.length > 0){
            for(Integer checkitemId : checkitemIds){
                Map<String,Integer> map = new HashMap<>();
                map.put("checkgroup_id", checkGroupId);
                map.put("checkitem_id", checkitemId);

                // 调用dao, 执行关联检查项组和检查项的关联表
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }
}
