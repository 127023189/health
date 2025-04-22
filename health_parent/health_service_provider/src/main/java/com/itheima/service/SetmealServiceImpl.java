package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.Setmeal;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;


import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService{
    @Autowired
    private SetmealDao setmealDao;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfiger;

    @Value("${out_put_path}")
    private String outputpath;
    /**
     * 添加套餐
     * @param setmeal
     * @param checkgroupIds
     */
    public void add(Setmeal setmeal, Integer[] checkgroupIds){
        setmealDao.add(setmeal);
        if(checkgroupIds!=null && checkgroupIds.length>0){
            setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
        }
        // 新增套餐后在添加至另一个Redis缓存中，两个集合相减就得到垃圾图片
        savePic2Redis(setmeal.getImg());

        // 当添加套餐后需要重新生成静态页面
        generateMobileStaticHtml();
    }

    public void generateMobileStaticHtml() {
        List<Setmeal> setmealList = this.findAll();
        //生成套餐列表静态页面
        generateMobileSetmealListHtml(setmealList);
        //生成套餐详情静态页面（多个）
        generateMobileSetmealDetailHtml(setmealList);
    }

    /**
     * 生成套餐详情静态页面（多个）
     * @param setmealList
     */
    private void generateMobileSetmealDetailHtml(List<Setmeal> setmealList) {
        for(Setmeal setmeal : setmealList){
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("setmeal",this.findById(setmeal.getId()));
            this.generateHtml("mobile_setmeal_detail.ftl",
                    "setmeal_detail_"+setmeal.getId()+".html",dataMap);
        }
    }

    /**
     * 生成套餐列表静态页面（一个
     * @param setmealList
     */
    private void generateMobileSetmealListHtml(List<Setmeal> setmealList) {
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("setmealList",setmealList);
        this.generateHtml("mobile_setmeal.ftl","m_setmeal.html",dataMap);
    }

    /**
     * 生成静态页面
     * @param templateName
     * @param htmlPageName
     * @param dataMap
     */
    public void generateHtml(String templateName, String htmlPageName, Map<String, Object> dataMap){
        // 获取模板
        Configuration configuration = freeMarkerConfiger.getConfiguration();
        Writer out = null;
        try {
            // 加载模板文件
            Template template = configuration.getTemplate(templateName);
            //生成数据
            File file = new File(outputpath+"\\"+htmlPageName);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

            // 生成静态页面
            template.process(dataMap,out);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(null!=out){
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage,pageSize);
        // 分页查询
        Page<Setmeal> page = setmealDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    public void savePic2Redis(String fileName){
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,fileName);
    }



    /**
     * 设置套餐和检查组的多对多关系
     * @param id
     * @param checkgroupIds
     */
    public void setSetmealAndCheckGroup(Integer id, Integer[] checkgroupIds){
        for(Integer checkgroupId : checkgroupIds){
            Map<String,Integer> map = new HashMap<>();
            map.put("setmeal_id",id);
            map.put("checkgroup_id",checkgroupId);
            setmealDao.setSetmealAndCheckGroup(map);
        }
    }
}
