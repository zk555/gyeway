package com.gy.gyeway.rpc.rpcService;

import com.gy.gyeway.base.cache.ProtocalStrategyCache;
import com.gy.gyeway.rpc.annotation.RPCService;
import com.gy.gyeway.rpc.dataBridge.ResponseData;
import com.gy.gyeway.server.Server2Terminal;

import java.util.ArrayList;
import java.util.List;
/**
 * class_name: RPCExportServiceImpl
 * package: com.gy.gyeway.rpc.rpcService
 * describe: 多规约
 * creat_user: zhaokai@
 * creat_date: 2019/5/30
 * creat_time: 17:43
 **/
@RPCService
public class RPCExportServiceImpl  implements RPCExportService{

    @Override
    public ResponseData test(String str) {

        System.out.println("............rpc 测试服务..............str="+str);
        ResponseData ret = new ResponseData();
        List<Object> list = new ArrayList<>(1);
        list.add(1111);
        ret.setData(list);
        return ret;
    }
    @Override
    public ResponseData getAllProtocal() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public ResponseData addNewProtocal(List<Integer> str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseData updateProtocalByPid(String pid , List<Integer> str) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseData delProtocalByPid(String port) {

        Server2Terminal server2Terminal = ProtocalStrategyCache.protocalServerCache.get(port);
        server2Terminal.close();
        return new ResponseData();
    }

    @Override
    public ResponseData startProtocalServiceByPid(String pid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseData stopProtocalServiceByPid(String pid) {
        // TODO Auto-generated method stub
        return null;
    }
}
