package com.gy.gyeway.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
