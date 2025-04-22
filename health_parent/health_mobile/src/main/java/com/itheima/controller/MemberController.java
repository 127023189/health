package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Reference
    private MemberService memberService;

    @Autowired
    private JedisPool jedisPool;

    /**
     * 登录，并将状态存入cookie中
     * @param response
     * @param map
     * @return
     */
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map map){
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");

        String codeRedis = jedisPool.getResource().get(telephone + ":code");

        if(codeRedis == null || !codeRedis.equals(validateCode)){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }else{
            // 看是不是会员
            Member member = memberService.findByTelephone(telephone);
            if(member == null){
                // 需要自动注册会员
                member = new Member();
                member.setPhoneNumber(telephone);
                member.setRegTime(LocalDate.now());
                memberService.add(member);
            }

            // 登录成功，将会员id存入cookie中，用于页面展示
            Cookie cookie = new Cookie("login_html_telephone",telephone);
            cookie.setPath("/"); // 根路径下
            cookie.setMaxAge(60 * 60 * 24 * 30); // 保存一个月
            response.addCookie(cookie);

            // 保存会员信到redis
            String json = JSON.toJSON(member).toString();

            // 保存到redis中,时间为30分钟
            jedisPool.getResource().setex(
                    telephone,60*30,json
            );
            return new Result(true, MessageConstant.LOGIN_SUCCESS);
        }
    }
}
