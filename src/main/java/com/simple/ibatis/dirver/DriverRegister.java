package com.simple.ibatis.dirver;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author xiabing
 * @Desc 驱动注册中心
 **/
public class DriverRegister {

    /*
     * mysql的driver类
     * */
    private static final String MYSQLDRIVER = "com.mysql.jdbc.Driver";

    /*
     * 构建driver缓存，存储已经注册了的driver的类型
     * */
    private static final Map<String,Driver> registerDrivers = new ConcurrentHashMap<>();

    /*
     * 初始化
     * */
    static {
        Enumeration<Driver> driverEnumeration = DriverManager.getDrivers();
        while (driverEnumeration.hasMoreElements()){
            Driver driver = driverEnumeration.nextElement();
            registerDrivers.put(driver.getClass().getName(),driver);
        }
    }

    /*
     * 加载mysql驱动
     * */
    public void loadMySql(){
        if(! registerDrivers.containsKey(MYSQLDRIVER)){
            loadDriver(MYSQLDRIVER);
        }
    }

    /*
     * 加载数据库驱动通用方法，并注册到registerDrivers
     * */
    public void loadDriver(String driverName){

        Class<?> driverType;
        try {
            driverType = Class.forName(driverName);
            registerDrivers.put(driverType.getName(),(Driver)driverType.newInstance());
        }catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
