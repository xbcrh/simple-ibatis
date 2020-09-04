package com.simple.ibatis.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * @Author xiabing
 * @Desc 池化线程池
 **/
public class PoolDataSource implements DataSource{

    private Integer maxActiveConnectCount = 10; // 最大活跃线程数

    private Integer maxIdleConnectCount = 10; // 最大空闲线程数

    private Long maxConnectTime = 30*1000L; // 连接最长使用时间

    private Integer waitTime = 2000; // 线程wait等待时间

    private NormalDataSource normalDataSource;

    private Queue<PoolConnection> activeConList = new LinkedList<>();

    private Queue<PoolConnection> idleConList = new LinkedList<>();

    public PoolDataSource(String driverClassName, String url, String userName, String passWord) {

        this(driverClassName,url,userName,passWord,10,10);

    }

    public PoolDataSource(String driverClassName, String url, String userName, String passWord,Integer maxActiveConnectCount,Integer maxIdleConnectCount) {

        this.normalDataSource = new NormalDataSource(driverClassName,url,userName,passWord);
        this.maxActiveConnectCount = maxActiveConnectCount;
        this.maxIdleConnectCount = maxIdleConnectCount;

    }
    /**
     * @Desc 获取连接时先从空闲连接列表中获取。若没有，则判断现在活跃连接是否已超过设置的最大活跃连接数，没超过，new一个
     * 若超过，则判断第一个连接是否已超时，若超时，则移除掉在新建。若未超时，则wait(）等待。
     **/
    @Override
    public Connection getConnection(){
        Connection connection = null;
        try {
            connection = doGetPoolConnection().connection;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return connection;
    }

    public void removeConnection(Connection connection){
        PoolConnection poolConnection = new PoolConnection(connection);
        doRemovePoolConnection(poolConnection);
    }

    private PoolConnection doGetPoolConnection() throws SQLException{
        PoolConnection connection = null;
        while (connection == null){
            synchronized (this){
                if(idleConList.size() < 1){
                    if(activeConList.size() < maxActiveConnectCount){
                        connection = new PoolConnection(normalDataSource.getConnection());
                    }else {
// 判断第一个连接是否超时
                        PoolConnection poolConnection = activeConList.peek();
                        if(System.currentTimeMillis() - poolConnection.getCheckOutTime() > maxConnectTime){
// 移除第一个活跃连接
                            PoolConnection timeOutConnect = activeConList.poll();
                            if(!timeOutConnect.connection.getAutoCommit()){
// 非自动提交，回滚操作
                                timeOutConnect.connection.rollback();
                            }
                            timeOutConnect = null;
// 新建一个连接
                            connection = new PoolConnection(normalDataSource.getConnection());
                        }else {
                            try{
                                this.wait(waitTime);
                            }catch (InterruptedException e){
// ignore
                                break;
                            }
                        }
                    }
                }else {
                    connection = idleConList.poll();
                }
                if(connection != null){
                    connection.setCheckOutTime(System.currentTimeMillis());
                    activeConList.add(connection);
                }
            }
        }

        return connection;
    }

    private void doRemovePoolConnection(PoolConnection connection){
        synchronized (this){
            activeConList.remove(connection);
            if(idleConList.size() < maxIdleConnectCount){
                idleConList.add(connection);
            }
            this.notifyAll();
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
