package com.simple.ibatis.execute;

import com.simple.ibatis.core.Config;
import com.simple.ibatis.datasource.PoolDataSource;
import com.simple.ibatis.mapper.User;
import com.simple.ibatis.mapper.UserMapper;
import org.junit.Test;

import java.sql.SQLException;
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
    public void shouldGetCache() throws SQLException {
        PoolDataSource poolDataSource = new PoolDataSource("com.mysql.jdbc.Driver","jdbc:mysql://101.132.150.75:3306/our-auth","root","root");
        Config config = new Config("com/simple/ibatis/mapper",poolDataSource);
        config.setOpenCache(true);

        Executor simpleExecutor = config.getExecutor();
        UserMapper userMapper = simpleExecutor.getMapper(UserMapper.class);

        User user = new User();
        user.setId(1);
        user.setName("root");
        List<User> userList = userMapper.getUsers(user);

        List<User> userList1 = userMapper.getUsers(user);

        simpleExecutor.close();
    }

    @Test
    public void shouldOpenTransaction() throws SQLException{
        /**基本配置*/
        PoolDataSource poolDataSource = new PoolDataSource("com.mysql.jdbc.Driver","jdbc:mysql://101.132.150.75:3306/our-auth","root","root");
        Config config = new Config("com/simple/ibatis/mapper",poolDataSource);
        /**设置为启用事务，关闭自动提交*/
        config.setOpenTransaction(true);

        /**获取执行器*/
        Executor simpleExecutor = config.getExecutor();
        UserMapper userMapper = simpleExecutor.getMapper(UserMapper.class);

        User user = new User();
        user.setId(1);
        user.setName("xiabing");
        /**更新名字为xiabing，但未提交*/
        userMapper.update(user);

        User user1 = userMapper.getUserById(1);
        /**获取ID为1的名字，为root，说明上文的语句还没有提交*/
        System.out.println(user1.getName());
        /**事务提交语句*/
        //simpleExecutor.commit();
    }
}
