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
    ResponseData getAllProtocal(boolean onlyRunning);


    /**
     * 新增规约
     * @param pid
     * @param str 共8位 =pId+ isBigEndian+ beginHexVal+ lengthFieldOffset+ lengthFieldLength+ isDataLenthIncludeLenthFieldLenth+ exceptDataLenth+ port
     * @param startAtOnce  是否立即启动服务
     * @return
     */
    ResponseData addNewProtocal(String pid ,List<Integer> str,boolean startAtOnce);


    /**
     * 更新规约
     * @param str
     * @return
     */
    ResponseData updateProtocalByPid(String pid,List<Integer> str);

    /**
     * 删除规约，删除之后不能再有其它操作
     * @param pId
     * @return
     */
    ResponseData delProtocalByPid(String pId);

    //----------------------------------------------------------
    /**
     * 通过指定端口开启被stop的网关服务
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

//---------------------------------------------------------
//    //高级功能模块
//    /**
//     * 通过自定义长度解析规则方式新增规约
//     * @param pid
//     * @param strategy 共7位 =pId+ isBigEndian+ beginHexVal+ lengthFieldOffset+ isDataLenthIncludeLenthFieldLenth+ exceptDataLenth+ port
//     * @param Content 方法体
//     * @return
//     */
//    ResponseData addStrategyByAppointParseMethod(String pid,List<Integer> strategy ,String Content);

}
