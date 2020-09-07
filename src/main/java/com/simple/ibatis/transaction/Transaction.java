package com.simple.ibatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author xiabing
 * @Desc 增加事务功能
 **/
public interface Transaction {

    Connection getConnection() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;
}