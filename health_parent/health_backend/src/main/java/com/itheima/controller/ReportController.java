package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计报表
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;

    @RequestMapping("/getMemberReport")
    public Result  getMemberReport(){
        List<String> list = new ArrayList<>();

        LocalDate now = LocalDate.now();

        // 获取之前的12个月
        YearMonth yearMonth = YearMonth.from(now.minusMonths(12));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM");

        for(int i = 0;i < 12;i++){
            // 获取当前时间
            YearMonth current = yearMonth.plusMonths(i);
            list.add(current.format(formatter));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("months",list);

        List<Integer> memberCount = memberService.findMemberCountByMonth(list);
        map.put("memberCount",memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);

    }
}
