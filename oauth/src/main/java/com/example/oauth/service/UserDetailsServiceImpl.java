package com.example.oauth.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lizhichao
 * @description
 * @date 2021/10/14 13:20
 */
@Service("UserDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private static Map<String,String> USER;

    //模拟用户表
    static {
        USER=new HashMap<>();
        USER.put("admin","$2a$10$xox4JztwJISO9eWlj85.YO5ugx3RjVQYXXQ.TyACcjseiyyPeb8Q6");
        USER.put("user1","$2a$10$xox4JztwJISO9eWlj85.YO5ugx3RjVQYXXQ.TyACcjseiyyPeb8Q6");
        USER.put("user2","$2a$10$xox4JztwJISO9eWlj85.YO5ugx3RjVQYXXQ.TyACcjseiyyPeb8Q6");
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (USER.containsKey(username)){

            return new User(username,USER.get(username),new ArrayList<>());
        }else {
            return null;
        }


    }



}
