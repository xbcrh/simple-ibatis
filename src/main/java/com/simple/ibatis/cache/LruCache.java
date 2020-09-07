package com.simple.ibatis.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xiabing
 * @description: 缓存包装类
 */
public class LruCache implements Cache{

    private static Integer cacheSize = 100;

    private static Float loadFactory = 0.75F;

    private Cache trueCache;

    private Map<String,Object> linkedCache;

    private static Map.Entry removeEntry;

    public LruCache(Cache trueCache){
        this(cacheSize,loadFactory,trueCache);
    }

    public LruCache(Integer cacheSize, Float loadFactory, Cache trueCache) {
        this.cacheSize = cacheSize;
        this.loadFactory = loadFactory;
        this.trueCache = trueCache;
        this.linkedCache = new LinkedHashMap<String, Object>(cacheSize,loadFactory,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                if(getSize() >  cacheSize){
                    removeEntry = eldest;
                    return true;
                }
                return false;
            }
        };
    }


    @Override
    public void putCache(String key, Object val) {
        this.trueCache.putCache(key,val);
        this.linkedCache.put(key,val);
        if(removeEntry != null){
            removeCache((String)removeEntry.getKey());
            removeEntry = null;
        }
    }

    @Override
    public Object getCache(String key) {
        linkedCache.get(key);
        return trueCache.getCache(key);
    }

    @Override
    public void cleanCache() {
        trueCache.cleanCache();
        linkedCache.clear();
    }

    @Override
    public int getSize() {
        return trueCache.getSize();
    }

    @Override
    public void removeCache(String key) {
        trueCache.removeCache(key);
    }
}
