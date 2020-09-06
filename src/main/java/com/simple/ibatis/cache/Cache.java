package com.simple.ibatis.cache;

/**
 * @author xiabing
 * @description: 自定义缓存接口
 */
public interface Cache {

    /**放入缓存*/
    void putCache(String key,Object val);

    /**获取缓存*/
    Object getCache(String key);

    /**清空缓存*/
    void cleanCache();

    /**获取缓存健数量*/
    int getSize();

    /**移除key的缓存*/
    void removeCache(String key);
}
