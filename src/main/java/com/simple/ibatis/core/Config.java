package com.simple.ibatis.core;

import com.simple.ibatis.datasource.PoolDataSource;
import com.simple.ibatis.mapping.MapperProxy;

import javax.sql.DataSource;

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

    public Config(String mapperSource,PoolDataSource dataSource){
        this.dataSource = dataSource;
        this.daoSource = mapperSource;
        this.mapperCore = new MapperCore(this);
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
}
