package com.nowcoder.community.utils;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser(){
        return users.get();
    }

    public void setUsers(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }
}
