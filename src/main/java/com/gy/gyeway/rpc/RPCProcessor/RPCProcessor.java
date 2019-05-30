package com.gy.gyeway.rpc.RPCProcessor;

import com.gy.gyeway.rpc.dataBridge.RequestData;
import com.gy.gyeway.rpc.dataBridge.ResponseData;

/**
 * RPC服务接口
 * @Description:
 * @author  zk
 */
public interface RPCProcessor {
    /**
     * 发布rpc服务
     * @throws Exception
     */
    void exportService() throws Exception ;

    /**
     * 调用rpc服务
     * @param requestData
     * @return
     */
    ResponseData executeService(RequestData requestData);
}
