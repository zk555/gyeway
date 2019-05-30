package com.gy.gyeway.base.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc服务缓存
 * @Description:
 * @author  zk
 */
public class RPCCache {
    /**
     * class_name: RPCCache
     * package: com.gy.gyeway.base.cache
     * describe: TODO ：rpc暴露方法的容器 ， key : className ,value : class
     * creat_user: zhaokai@
     * creat_date: 2019/5/30
     * creat_time: 18:40
     **/
    private static ConcurrentHashMap<String, Class<?>> rpcServices;

    private RPCCache() {
        throw new AssertionError();
    }

    static {
        rpcServices = new ConcurrentHashMap<String, Class<?>>();
    }

    public static Class<?> getClass(String className) {
        return rpcServices.get(className);
    }

    public static void putClass(String className, Class<?> clazz) {
        rpcServices.put(className, clazz);
    }

}
