package com.gy.gyeway.rpc.RPCProcessor;

import com.gy.gyeway.base.cache.RPCCache;
import com.gy.gyeway.rpc.annotation.RPCService;
import com.gy.gyeway.rpc.dataBridge.RequestData;
import com.gy.gyeway.rpc.dataBridge.ResponseData;

import java.util.List;

public class RPCProcessorImpl  implements RPCProcessor {

    @Override
    public void exportService() throws Exception {
        List<String> result = gate.util.MixAll.getClazzName("com.gy.gyeway.rpc.rpcService",false);
        for (String className : result) {
            Class clazz = Class.forName(className);
            if(clazz.isAnnotationPresent(RPCService.class)){
                RPCCache.putClass(className, clazz);
            }
        }



    }

    @Override
    public ResponseData executeService(RequestData requestData) {
        // TODO Auto-generated method stub
        return null;
    }



    public static void main(String[] args) {
        gate.util.MixAll.getClazzName("com.gy.gyeway.rpc.rpcService",false);
    }
}
