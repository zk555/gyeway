package com.gy.gyeway.rpc;

import com.gy.gyeway.rpc.dataBridge.RequestData;
import com.gy.gyeway.rpc.dataBridge.ResponseData;

/**
 * RPC服务接口
 * @Description:
 * @author  zk
 *
 */
public interface RPCService {

    void exportService ();

    ResponseData executeService(RequestData requestData);
}
