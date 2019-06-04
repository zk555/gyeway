package com.gy.gyeway.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * class_name: BasicThreadPoolTaskExecutor
 * package: com.gy.gyeway.concurrent
 * describe: TODO 基本线程池
 * creat_user: zhaokai@
 * creat_date: 2019/6/4
 * creat_time: 14:48
 **/
public class BasicThreadPoolTaskExecutor {
    private static ExecutorService service=null;
    private BasicThreadPoolTaskExecutor(){
        throw new AssertionError();
    }

    static{
        service = Executors.newCachedThreadPool(new ThreadFactoryImpl("basicExecutor_", false));
    }

    public static ExecutorService getBasicExecutor(){
        return service;
    }
}
