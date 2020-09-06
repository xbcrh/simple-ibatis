package com.simple.ibatis.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiabing
 * @description: 缓存的简单实现
 */
public class SimpleCache implements Cache{

    private static Map<String,Object> map = new HashMap<>();


    @Override
    public void putCache(String key, Object val) {
        map.put(key,val);
    }

    @Override
    public Object getCache(String key) {
        return map.get(key);
    }

    @Override
    public void cleanCache() {
        map.clear();
    }

    @Override
    public int getSize() {
        return map.size();
    }

    @Override
    public void removeCache(String key) {
        map.remove(key);
    }
}
