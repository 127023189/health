package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {

    @Reference
    private UserService userService;

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.itheima.pojo.User user = userService.findByUsername(username);
        if(user == null){
            return null;
        }

        List<GrantedAuthority> list = new ArrayList<>();
        Set<Role> roles = user.getRoles();
        for(Role role : roles){
            // 授予角色
            list.add(new SimpleGrantedAuthority(role.getKeyword()));
            Set<Permission> permissions = role.getPermissions();
            for(Permission permission : permissions){
                list.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }
       UserDetails userDetails = new User(username,user.getPassword(),list);
        return userDetails;
    }
}
