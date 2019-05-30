package com.gy.gyeway.rpc.RPCProcessor;

import com.gy.gyeway.base.cache.RPCCache;
import com.gy.gyeway.remoting.RemoteServer;
import com.gy.gyeway.rpc.annotation.RPCService;
import com.gy.gyeway.rpc.dataBridge.RequestData;
import com.gy.gyeway.rpc.dataBridge.ResponseData;
import com.gy.gyeway.utils.MixAll;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 远程服务发布类
 */
public class RPCProcessorImpl  implements RPCProcessor {

    /**
     * 使用@RPCService注解把请求存到map
     * @throws Exception
     */
    @Override
    public void exportService() throws Exception {
        List<String> result = MixAll.getClazzName("com.gy.gyeway.rpc.rpcService",false);
        for (String className : result) {
            Class<?> clazz = Class.forName(className);
            if(clazz.isAnnotationPresent(RPCService.class)){
                RPCCache.putClass(clazz.getSimpleName(), clazz);
            }
        }
        new RemoteServer().start();
        System.out.println("发布rpc服务完毕........");
    }

    /**
     * client发送请求，在缓存容器中拿到实现，利用反射调用
     * @param requestData
     * @return
     */
    @Override
    public ResponseData executeService(RequestData requestData) {
        Class<?> clazz = RPCCache.getClass(requestData.getClassName()+"Impl");
        ResponseData responseData = null;
        try {
            Method method = clazz.getMethod(requestData.getMethodName(), requestData.getParamTyps());
            responseData = (ResponseData) method.invoke(clazz.newInstance(), requestData.getArgs());
            //请求响应代码一一对应
            responseData.setResponseNum(requestData.getRequestNum());
            return responseData;
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        responseData = new ResponseData();
        responseData.setResponseNum(requestData.getRequestNum());
        return null;
    }



    public static void main(String[] args) {
        MixAll.getClazzName("com.gy.gyeway.rpc.rpcService",false);
    }
}
