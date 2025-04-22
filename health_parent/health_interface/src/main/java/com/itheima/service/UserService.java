package com.itheima.service;

import com.itheima.pojo.User;

public interface UserService {
    public abstract User findByUsername(String username);
}
