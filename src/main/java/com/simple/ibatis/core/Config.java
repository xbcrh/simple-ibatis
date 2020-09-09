package com.simple.ibatis.core;

import com.simple.ibatis.datasource.PoolDataSource;
import com.simple.ibatis.execute.Executor;
import com.simple.ibatis.execute.SimpleExecutor;

/**
 * @Author xiabing
 * @Desc simple batis core config
 **/
public class Config {

    // 数据源
    private PoolDataSource dataSource;

    // mapper包地址，后续改为List<String>，能同时加载多个mapper包 todo
    private String daoSource;

    // mapper核心文件
    private MapperCore mapperCore;

    // 是否启用事务
    private boolean openTransaction;

    // 是否开启缓存
    private boolean openCache;

    public Config(String mapperSource,PoolDataSource dataSource){
        this.dataSource = dataSource;
        this.daoSource = mapperSource;
        this.mapperCore = new MapperCore(this);
    }

    public Executor getExecutor(){
        return new SimpleExecutor(this,this.getDataSource(),openTransaction,openCache);
    }

    public PoolDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(PoolDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getDaoSource() {
        return daoSource;
    }

    public void setDaoSource(String daoSource) {
        this.daoSource = daoSource;
    }

    public MapperCore getMapperCore() {
        return mapperCore;
    }

    public void setMapperCore(MapperCore mapperCore) {
        this.mapperCore = mapperCore;
    }

    public boolean isOpenTransaction() {
        return openTransaction;
    }

    public void setOpenTransaction(boolean openTransaction) {
        this.openTransaction = openTransaction;
    }

    public boolean isOpenCache() {
        return openCache;
    }

    public void setOpenCache(boolean openCache) {
        this.openCache = openCache;
    }

}