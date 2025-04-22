package com.itheima.service;

import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import com.itheima.utils.MD5Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemberServiceImpI implements MemberService{

    private MemberDao memberDao;

    @Override
    public Member findByTelephone(String telephone) {
       return memberDao.findByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
        if(member.getPassword() != null){
            // 加密密码
            member.setPassword(MD5Utils.md5(member.getPassword()));
        }

        memberDao.add(member);
    }

    @Override
    public List<Integer> findMemberCountByMonth(List<String> monthList) {
        List<Integer> list = new ArrayList<>();
        for(String month : monthList){
            Integer count = memberDao.findMemberCountByMonth(month);
            list.add(count);
        }
        return list;
    }
}
