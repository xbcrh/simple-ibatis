package com.simple.ibatis.transaction;

import com.simple.ibatis.datasource.PoolDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author xiabing
 * @Desc 事务的简单实现
 **/
public class SimpleTransaction implements Transaction{

    private Connection connection; // 数据库连接
    private PoolDataSource dataSource; // 数据源
    private Integer level; // 事务隔离级别
    private Boolean autoCommmit; // 是否自动提交

    public SimpleTransaction(PoolDataSource dataSource){
        this(dataSource,null,null);
    }

    public SimpleTransaction(PoolDataSource dataSource, Integer level, Boolean autoCommmit) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommmit = autoCommmit;
    }

    @Override
    public Connection getConnection() throws SQLException{
        this.connection = dataSource.getConnection();
        if(autoCommmit != null){
            this.connection.setAutoCommit(autoCommmit);
        }
        if(level != null){
            this.connection.setTransactionIsolation(level);
        }
        return this.connection;
    }

    @Override
    public void commit() throws SQLException{
        this.connection.commit();
    }

    @Override
    public void rollback() throws SQLException{
        this.connection.rollback();
    }

    @Override
    public void close() throws SQLException{
        dataSource.removeConnection(connection);
    }
}