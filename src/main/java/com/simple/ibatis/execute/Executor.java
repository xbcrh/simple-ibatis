package com.simple.ibatis.execute;

import java.util.List;

public interface Executor {
    /**
     * 根据statement,parameter查询单条数据
     * @return 单条语句
     **/
    <T> T select(String statement,Object parameter);

    /**
     * 根据statement,parameter查询多条数据
     * @return 多条语句
     **/
    <E> List<E> selectList(String statement,Object parameter);

    /**
     * 根据statement,parameter更新数据
     * @return 受影响的条数
     **/
    int update(String statement,Object parameter);

    /**
     * 根据statement,parameter删除数据
     * @return 受影响的条数
     **/
    int delete(String statement,Object parameter);
}
