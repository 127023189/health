package com.itheima.dao;

import com.itheima.pojo.OrderSetting;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {
    long findCountByOrderDate(LocalDate orderDate);

    void editNumberByOrderDate(OrderSetting orderSetting);

    void add(OrderSetting orderSetting);

    List<OrderSetting> getOrderSettingByMonth(Map date);

    OrderSetting findByOrderDate(LocalDate date);

    void editReservationsByOrderDate(OrderSetting orderSetting);
}
