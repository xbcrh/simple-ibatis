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
    private Integer level = Connection.TRANSACTION_REPEATABLE_READ;; // 事务隔离级别
    private Boolean autoCommmit = true; // 是否自动提交

    public SimpleTransaction(PoolDataSource dataSource){
        this(dataSource,null,null);
    }

    public SimpleTransaction(PoolDataSource dataSource, Integer level, Boolean autoCommmit) {
        this.dataSource = dataSource;
        if(level != null){
            this.level = level;
        }
        if(autoCommmit != null){
            this.autoCommmit = autoCommmit;
        }
    }

    @Override
    public Connection getConnection() throws SQLException{
        this.connection = dataSource.getConnection();

        this.connection.setAutoCommit(autoCommmit);

        this.connection.setTransactionIsolation(level);

        return this.connection;
    }

    @Override
    public void commit() throws SQLException{
        if(this.connection != null){
            this.connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException{
        if(this.connection != null){
            this.connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException{
        if(!autoCommmit && connection != null){
           connection.rollback();
        }

        if(connection != null){
            dataSource.removeConnection(connection);
        }
        this.connection = null;
    }
}