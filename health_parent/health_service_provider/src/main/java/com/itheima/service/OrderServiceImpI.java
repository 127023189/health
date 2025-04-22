package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpI implements OrderService{

    // 预约设置++
    @Autowired
    private OrderSettingDao orderSettingDao;

    // 会员
    @Autowired
    private MemberDao memberDao;

    // 预约
    @Autowired
    private OrderDao orderDao;


    @Override
    public Result order(Map map) throws Exception {
        // 检查这一天是否能预约
        String orderDate = (String) map.get("orderDate");
        LocalDate date = LocalDate.parse(orderDate);
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(date);

        if(orderSetting == null ){
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }

        // 检查预约是否满人
        int number = orderSetting.getNumber();
        int reservations = orderSetting.getReservations();

        if(reservations >= number){
            // 预约已满
            return new Result(false, MessageConstant.ORDER_FULL);
        }

        // 检查是否是会员
        String telephone = (String) map.get("telephone");
        Member member = memberDao.findByTelephone(telephone);

        if(member != null){
            // 是会员
            Integer memberId = member.getId();
            // 判断是否是重复预约
            int setmealId = Integer.parseInt((String) map.get("setmealId"));
            Order order = new Order(memberId, date, null, null, setmealId);
            // 查询是否是重复预约
            List<Order> list = orderDao.findByCondition(order);
            if(list != null && list.size() > 0){
                return new Result(false, MessageConstant.HAS_ORDERED);
            }
        }

        // 可以预约,已预约人数+1
        orderSetting.setReservations(orderSetting.getReservations() + 1);
        orderSettingDao.editReservationsByOrderDate(orderSetting);

        if(member == null){
            // 不是会员，自动注册
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(LocalDate.now());
            memberDao.add(member);
        }

        //保存预约信息导预约表中
        Order order = new Order(
                member.getId(),
                date,
                (String) map.get("orderType"),
                Order.ORDERSTATUS_NO,
                Integer.parseInt((String) map.get("setmealId"))
        );
        orderDao.add(order);

        return new Result(true, MessageConstant.ORDER_SUCCESS,order.getId());
    }

    @Override
    public Map findById(Integer id) throws Exception{
        Map map = orderDao.findById4Detail(id);
        if(map != null){
            LocalDate date = LocalDate.parse((String) map.get("orderDate"));
            map.put("orderDate",date.toString());
        }

        return map;
    }
}
