package com.simple.ibatis.mapper;

import com.simple.ibatis.annotation.Dao;
import com.simple.ibatis.annotation.Select;
import com.simple.ibatis.annotation.Update;

import java.util.List;

/**
 * @author xiabing
 * @description: TODO
 */
@Dao
public interface UserMapper {

    @Select("select * from sys_user where id = {user.id} and name = {user.name}")
    List<User> getUsers(User user);

    @Update("update sys_user set name = {user.name} where  id = {user.id}")
    int update(User user);

    @Select("select * from sys_user where id = {id}")
    User getUserById(Integer id);
}
