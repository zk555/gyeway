package com.gy.gyeway.base.cache;

import com.gy.gyeway.base.domain.LocalCache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存zookeeper中master的节点信息
 */
public class ZKMasterNodeLocalCache implements LocalCache {

    /**
     *  zk 缓存到 ConcurrentHashMap 中
     */
    private ConcurrentHashMap<String, String> zknodeCache = null;

    private ZKMasterNodeLocalCache(){
        if(inner.zkLocalCache != null){
            throw new IllegalStateException("禁止创建gate.cluster.ZKlocalCache对象！");
        }
        zknodeCache = new ConcurrentHashMap<>();
    }


    static class inner{
        static ZKMasterNodeLocalCache zkLocalCache = new ZKMasterNodeLocalCache();

    }


    @Override
    public Object get(Object key) {
        return zknodeCache.get(key);
    }

    @Override
    public void set(Object key, Object value) {
        zknodeCache.put(key.toString(), value.toString());
    }

    @Override
    public boolean del(Object key) {
        return zknodeCache.remove(key) == null;
    }

    public static ZKMasterNodeLocalCache getInstance(){
        return ZKMasterNodeLocalCache.inner.zkLocalCache;
    }



}
