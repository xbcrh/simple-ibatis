package com.simple.ibatis.datasource;

import java.sql.Connection;

/**
 * @Author xiabing
 * @Desc 连接代理类
 **/
public class PoolConnection {

    public Connection connection;

    private Long CheckOutTime;

    private int hashCode = 0;

    public PoolConnection(Connection connection) {
        this.connection = connection;
        this.hashCode = connection.hashCode();
    }

    public Long getCheckOutTime() {
        return CheckOutTime;
    }

    public void setCheckOutTime(Long checkOutTime) {
        CheckOutTime = checkOutTime;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PoolConnection){
            return connection.hashCode() ==
                    ((PoolConnection) obj).connection.hashCode();
        }else if(obj instanceof Connection){
            return obj.hashCode() == hashCode;
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
