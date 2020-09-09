package com.simple.ibatis.execute;

import com.simple.ibatis.cache.Cache;
import com.simple.ibatis.cache.LruCache;
import com.simple.ibatis.cache.SimpleCache;
import com.simple.ibatis.core.Config;
import com.simple.ibatis.core.MapperCore;
import com.simple.ibatis.datasource.PoolDataSource;
import com.simple.ibatis.mapping.MapperProxy;
import com.simple.ibatis.statement.PreparedStatementHandle;
import com.simple.ibatis.statement.ResultSetHandle;
import com.simple.ibatis.transaction.Transaction;
import com.simple.ibatis.transaction.TransactionFactory;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.List;

public class SimpleExecutor implements Executor{

    public Config config;

    public MapperCore mapperCore;

    public PoolDataSource poolDataSource;

    public Transaction transaction;

    public Cache cache;

    public SimpleExecutor(Config config,PoolDataSource poolDataSource){
        this(config,poolDataSource,false,false);
    }

    public SimpleExecutor(Config config,PoolDataSource poolDataSource,boolean openTransaction,boolean openCache){
        this.config = config;
        this.mapperCore = config.getMapperCore();
        this.poolDataSource = poolDataSource;
        if(openCache){
            this.cache = new LruCache(new SimpleCache());
        }
        if(openTransaction){
            this.transaction = TransactionFactory.newTransaction(poolDataSource,Connection.TRANSACTION_REPEATABLE_READ,false);
        }else {
            this.transaction = TransactionFactory.newTransaction(poolDataSource,null,null);
        }
    }

    public <T> T getMapper(Class<T> type){
        MapperProxy mapperProxy = new MapperProxy(type,this);
        return (T)mapperProxy.initialization();
    }

    public <E> List<E> select(Method method,Object[] args) throws Exception{
        String cacheKey = generateCacheKey(method,args);
        if(cache != null && cache.getCache(cacheKey) != null){
            System.out.println("this is cache");
            return (List<E>)cache.getCache(cacheKey);
        }

        PreparedStatementHandle preparedStatementHandle = new PreparedStatementHandle(mapperCore,transaction,method,args);
        PreparedStatement preparedStatement = preparedStatementHandle.generateStatement();
        ResultSet resultSet = null;
        preparedStatement.executeQuery();
        resultSet = preparedStatement.getResultSet();

        Class returnClass = mapperCore.getReturnType(method);
        if(returnClass == null || void.class.equals(returnClass)){
            preparedStatement.close();
            return null;
        }else {
            ResultSetHandle resultSetHandle = new ResultSetHandle(returnClass,resultSet);
            List<E> res = resultSetHandle.handle();
            if(cache != null){
                cache.putCache(cacheKey,res);
            }
            preparedStatement.close();
            resultSet.close();
            return res;
        }
    }

    public int update(Method method,Object[] args)throws SQLException{
        PreparedStatementHandle preparedStatementHandle = null;
        PreparedStatement preparedStatement = null;
        Integer count = 0;

        if(cache != null){
            cache.cleanCache();
        }
        try{
            preparedStatementHandle = new PreparedStatementHandle(mapperCore,transaction,method,args);
            preparedStatement = preparedStatementHandle.generateStatement();
            count = preparedStatement.executeUpdate();
        }finally {
            if(preparedStatement != null){
                preparedStatement.close();
            }
        }

        return count;
    }

    @Override
    public void commit() throws SQLException {
        transaction.commit();
    }

    @Override
    public void rollback() throws SQLException {
        transaction.rollback();
    }

    @Override
    public void close() throws SQLException {
        transaction.close();
    }

    private String generateCacheKey(Method method, Object args[]){
        StringBuilder cacheKey = new StringBuilder(method.getDeclaringClass().getName() + method.getName());
        for(Object o:args){
            cacheKey.append(o.toString());
        }
        return cacheKey.toString();
    }
}