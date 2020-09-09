package com.simple.ibatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author xiabing
 * @Desc 增加事务功能
 **/
public interface Transaction {
    /**获取链接*/
    Connection getConnection() throws SQLException;
    /**提交*/
    void commit() throws SQLException;
    /**回滚*/
    void rollback() throws SQLException;
    /**关闭*/
    void close() throws SQLException;
}