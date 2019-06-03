package com.gy.gyeway.rpc.rpcService;

import com.gy.gyeway.rpc.dataBridge.ResponseData;

import java.util.List;

/**
 * rpc服务接口
 * @Description:
 * @author  zk
 */
public interface RPCExportService {
    /**
     * 测试用rpc
     * @param str
     * @return
     */
    ResponseData test(String str);

    /**
     * 获取当前网关所支持的所有规约信息
     * @return
     */
    ResponseData getAllProtocal();


    /**
     * 新增规约
     * @param str
     * @return
     */
    ResponseData addNewProtocal(List<Integer> str);

    /**
     * 更新规约
     * @param str
     * @return
     */
    ResponseData updateProtocalByPid(String pid,List<Integer> str);

    /**
     * 删除规约
     * @param pId
     * @return
     */
    ResponseData delProtocalByPid(String pId);

    //----------------------------------------------------------
    /**
     * 通过指定端口开启相关网关服务
     * @param pId
     * @return
     */
    ResponseData startProtocalServiceByPid(String pId);

    /**
     * 通过指定端口关闭相关网关服务
     * @param pId
     * @return
     */
    ResponseData stopProtocalServiceByPid(String pId);


}
