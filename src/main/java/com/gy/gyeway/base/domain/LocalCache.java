package com.gy.gyeway.base.domain;

/**
 * class_name: LocalCache
 * package: com.gy.gyeway.base.domain
 * describe: TODO 本地缓存统一接口
 * creat_user: zhaokai@
 * creat_date: 2019/6/4
 * creat_time: 14:31
 **/
public interface LocalCache {

    Object get(Object key);

    void set(Object key ,Object value);

    boolean del(Object key);
}
