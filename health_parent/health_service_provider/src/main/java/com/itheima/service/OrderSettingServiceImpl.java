package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@org.springframework.stereotype.Service
@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService{

    @Autowired
    private OrderSettingDao orderSettingDao;

    public void add(List<OrderSetting> list){
        if(list != null && list.size() > 0){
            for(OrderSetting orderSetting : list){
                // 判断当前日期是否已经进行了预约设置
                long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if(count > 0){
                    // 已存在执行更新
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else{
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    /**
     * 根据月份查询预约设置
     * @param date
     * @return
     */
    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        Map map = new HashMap();
        map.put("date",date);
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        List<Map> data = new ArrayList<>();
        for(OrderSetting orderSetting : list){
            Map orderMap = new HashMap();
            orderMap.put("date",orderSetting.getOrderDate().getDayOfMonth());
            orderMap.put("number",orderSetting.getNumber());
            orderMap.put("reservations",orderSetting.getReservations());
            data.add(orderMap);
        }
        return data;
    }

    @Override
    public void editNumberByOrderDate(OrderSetting orderSetting) {

        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        System.out.println("count = " + count);
        if(count > 0){
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else{
            orderSettingDao.add(orderSetting);
        }
    }

}
