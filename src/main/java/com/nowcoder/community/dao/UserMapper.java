package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String userName);

    User selectByEmail(String Email);

    int insert(User user);

    int updateStatus(@Param("id") int id, @Param("status") int status);

    int updateHeaderUrl(@Param("id") int id, @Param("headerUrl") String headerUrl);

    int updatePassword(@Param("id") int id, @Param("password") String password);
}
