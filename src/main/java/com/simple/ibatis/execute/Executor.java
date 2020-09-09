package com.simple.ibatis.execute;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    <T> T getMapper(Class<T> type);

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;
}
