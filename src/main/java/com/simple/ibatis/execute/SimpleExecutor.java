package com.simple.ibatis.execute;

import com.simple.ibatis.core.Config;
import com.simple.ibatis.core.MapperCore;
import com.simple.ibatis.datasource.PoolDataSource;
import com.simple.ibatis.mapping.MapperProxy;
import com.simple.ibatis.statement.PreparedStatementHandle;
import com.simple.ibatis.statement.ResultSetHandle;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor{

    public Config config;

    public MapperCore mapperCore;

    public PoolDataSource poolDataSource;

    public SimpleExecutor(Config config,PoolDataSource poolDataSource){
        this.config = config;
        this.mapperCore = config.getMapperCore();
        this.poolDataSource = poolDataSource;
    }

    public <T> T getMapper(Class<T> type){
        MapperProxy mapperProxy = new MapperProxy(type,this);
        return (T)mapperProxy.initialization();
    }

    public <E> List<E> select(Method method,Object[] args) throws Exception{
        List<E> list = new ArrayList<>();
        PreparedStatementHandle preparedStatementHandle = new PreparedStatementHandle(mapperCore,method,poolDataSource,args);
        PreparedStatement preparedStatement = preparedStatementHandle.generateStatement();
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();
        preparedStatementHandle.closeConnection();

        Class returnClass = mapperCore.getReturnType(method);
        if(returnClass == null || void.class.equals(returnClass)){
            return null;
        }else {
            ResultSetHandle resultSetHandle = new ResultSetHandle(returnClass,resultSet);
            return resultSetHandle.handle();
        }
    }

    public int update(Method method,Object[] args)throws SQLException{
        PreparedStatementHandle preparedStatementHandle = new PreparedStatementHandle(mapperCore,method,poolDataSource,args);
        PreparedStatement preparedStatement = preparedStatementHandle.generateStatement();
        Integer count = preparedStatement.executeUpdate();
        preparedStatementHandle.closeConnection();
        return count;
    }

    @Override
    public <T> T select(String statement, Object parameter) {
        return null;
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return null;
    }

    @Override
    public int update(String statement, Object parameter) {
        return 0;
    }

    @Override
    public int delete(String statement, Object parameter) {
        return 0;
    }
}
