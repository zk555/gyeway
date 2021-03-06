package com.gy.gyeway.base.cache;

import com.gy.gyeway.concurrent.ThreadFactoryImpl;
import com.gy.gyeway.server.Server2Terminal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProtocalStrategyCache {
    private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryImpl("persistent_ProtocalStrategy_", true));
    private ProtocalStrategyCache(){
        throw new AssertionError();
    }
    /**
     * 网关规约规则缓存---缓存当前网关支持的所有规约类型的  匹配规则，通过"规约编号"唯一标识--key=pId
     */
    public static ConcurrentHashMap<String, String> protocalStrategyCache ;
    /**
     * 网关规约服务缓存---用于规约服务控制--key=pId  (把启动的服务放到容器，以便后续操作如：close())
     */
    public static ConcurrentHashMap<String, Server2Terminal> protocalServerCache ;
    /**
     * 缓存IOTGate高级功能中得长度域解析器 Class地址
     * key = Pid
     * value = 全类名
     */
    public static ConcurrentHashMap<String, String> protocalStrategyClassUrlCache ;

    static{
        protocalStrategyCache = new ConcurrentHashMap<>();
        protocalServerCache = new ConcurrentHashMap<>();
        protocalStrategyClassUrlCache = new ConcurrentHashMap<>();
    }

    /**
     * 规约规则落地
     */
    private static void persistentProtocalStrategy(){
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // TODO 规约规则落地

            }
        }, 2, 3, TimeUnit.MINUTES);
    }

}
