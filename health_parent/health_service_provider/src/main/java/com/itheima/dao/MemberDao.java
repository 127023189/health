package com.itheima.dao;

import com.itheima.pojo.Member;

public interface MemberDao {
    void add(Member member);

    Member findByTelephone(String telephone);

    Integer findMemberCountByMonth(String date);
}
