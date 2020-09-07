package com.simple.ibatis.execute;

import com.simple.ibatis.core.Config;
import com.simple.ibatis.datasource.PoolDataSource;
import com.simple.ibatis.mapper.User;
import com.simple.ibatis.mapper.UserMapper;
import org.junit.Test;

import java.util.List;

/**
 * @author xiabing
 * @description: test
 */
public class ExecutorTest {

    @Test
    public void shouldConnect(){
        PoolDataSource poolDataSource = new PoolDataSource("com.mysql.jdbc.Driver","jdbc:mysql://101.132.150.75:3306/our-auth","root","root");
        ExecutorFactory executorFactory = new ExecutorFactory("com/simple/ibatis/mapper",poolDataSource);

        SimpleExecutor simpleExecutor = executorFactory.getExecutor();
        UserMapper userMapper = simpleExecutor.getMapper(UserMapper.class);

        User user = new User();
        user.setId(1);
        user.setName("root");
        List<User> userList = userMapper.getUsers(user);

        System.out.println(userList.get(0).getId());
    }

    @Test
    public void shouldConnectNew(){
        PoolDataSource poolDataSource = new PoolDataSource("com.mysql.jdbc.Driver","jdbc:mysql://101.132.150.75:3306/our-auth","root","root");
        Config config = new Config("com/simple/ibatis/mapper",poolDataSource);
        config.setOpenCache(true);

        SimpleExecutor simpleExecutor = config.getExecutor();
        UserMapper userMapper = simpleExecutor.getMapper(UserMapper.class);

        User user = new User();
        user.setId(1);
        user.setName("root");
        List<User> userList = userMapper.getUsers(user);

        user.setName("xiabing");
        userMapper.update(user);
        List<User> userList1 = userMapper.getUsers(user);
        System.out.println(userList.get(0).getId());
    }
}
