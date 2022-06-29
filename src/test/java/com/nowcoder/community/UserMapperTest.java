package com.nowcoder.community;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    UserMapper userMapper;

    @Test
    void selectByIdTest(){
        int id = 101;
        User user = userMapper.selectById(id);
        System.out.println(user);
    }

    @Test
    void selectByNameTest(){
        String name = "aaa";
        User user = userMapper.selectByName(name);
        System.out.println(user);
    }

    @Test
    void selectByEmailTest(){
        String email = "nowcoder101@sina.com";
        User user = userMapper.selectByEmail(email);
        System.out.println(user);
    }

    @Test
    void insertTest(){
        User user = new User();
        user.setUsername("llliiz6");
        user.setPassword("iufgfdeef");
        user.setCreateTime(new Date());
        user.setEmail("nowcoder101@bjtu.edu.com");
        user.setType(1);
        user.setSalt("49f10");
        user.setStatus(1);
        int res = userMapper.insert(user);
        assert res > 0;
        User resUser = userMapper.selectByName("llliiz6");
        System.out.println(resUser.getId().equals(user.getId()));
        System.out.println(user.getId());
    }


}
