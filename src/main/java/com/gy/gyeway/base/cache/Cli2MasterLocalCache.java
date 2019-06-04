package com.gy.gyeway.base.cache;

import com.gy.gyeway.base.domain.LocalCache;
import com.gy.gyeway.client.Client2Master;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存网关与前置连接的会话
 */
public class Cli2MasterLocalCache implements LocalCache {

    private ConcurrentHashMap<String, Client2Master> client2MasterCache = null;

    private Cli2MasterLocalCache(){
        if(inner.cli2MasterLocalCache != null){
            throw new IllegalStateException("禁止创建gate.base.cache.Cli2MasterLocalCache对象！");
        }
        client2MasterCache = new ConcurrentHashMap<>();
    }


    static class inner{
        static Cli2MasterLocalCache cli2MasterLocalCache = new Cli2MasterLocalCache();

    }


    @Override
    public Object get(Object key) {
        return client2MasterCache.get(key);
    }

    @Override
    public void set(Object key, Object value) {
        client2MasterCache.put(key.toString(),(Client2Master) value);
    }

    @Override
    public boolean del(Object key) {
        return client2MasterCache.remove(key) == null;
    }

    public static Cli2MasterLocalCache getInstance(){
        return Cli2MasterLocalCache.inner.cli2MasterLocalCache;
    }




}
