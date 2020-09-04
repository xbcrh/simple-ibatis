package com.simple.ibatis.execute;

import com.simple.ibatis.core.Config;
import com.simple.ibatis.datasource.PoolDataSource;


/**
 * @Author xiabing
 * @Desc 执行器工厂类
 **/
public class ExecutorFactory {

    public Config config;

    public ExecutorFactory(String mapperSource, PoolDataSource dataSource){

        this.config = new Config(mapperSource,dataSource);
    }

    public SimpleExecutor getExecutor(){

        return new SimpleExecutor(this.config,this.config.getDataSource());

    }
}
