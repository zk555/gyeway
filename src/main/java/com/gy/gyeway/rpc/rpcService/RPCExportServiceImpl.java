package com.gy.gyeway.rpc.rpcService;

import com.gy.gyeway.base.cache.ProtocalStrategyCache;
import com.gy.gyeway.rpc.annotation.RPCService;
import com.gy.gyeway.rpc.dataBridge.ResponseData;
import com.gy.gyeway.server.Server2Terminal;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.*;

/**
 * class_name: RPCExportServiceImpl
 * package: com.gy.gyeway.rpc.rpcService
 * describe: 多规约--需要考虑ProtocalStrategyCache缓存同步
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
    public ResponseData updateProtocalByPid(String pid , List<Integer> str) {
        ResponseData responseData = new ResponseData();
        if(pid == null|| "".equals(pid) || str == null || str.isEmpty() ){
            responseData.setReturnCode(500);
            responseData.setErroInfo(new IllegalArgumentException("参数不正确,请检查参数设置！"));
            return responseData;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.size(); i++) {
            sb.append(str.get(i)+",");
        }
        String newStraPro = sb.toString();
        ProtocalStrategyCache.protocalStrategyCache.replace(pid, newStraPro.substring(0, newStraPro.length()-1));
		stopProtocalServiceByPid(pid);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//3.启动服务
		startProtocalServiceByPid(pid);
        return responseData;
    }

    /**
     * 暂停某个规约服务
     * @param pid
     * @return
     */
    @Override
    public ResponseData delProtocalByPid(String pid) {
        ResponseData responseData = new ResponseData();
        if(pid == null|| "".equals(pid) ){
            responseData.setReturnCode(500);
            responseData.setErroInfo(new IllegalArgumentException("参数不正确,请检查参数设置！"));
            return responseData;
        }
        stopProtocalServiceByPid(pid);
        ProtocalStrategyCache.protocalStrategyCache.remove(pid);
        return responseData;
    }

    /**
     * 启动规约思想：读取规则传到netty解码器后启动netty服务
     * @param pid
     * @return
     */
    @Override
    public ResponseData startProtocalServiceByPid(String pid) {
        ResponseData responseData = new ResponseData();
        if(pid == null|| "".equals(pid) ){
            responseData.setReturnCode(500);
            responseData.setErroInfo(new IllegalArgumentException("参数不正确,请检查参数设置！"));
            return responseData;
        }
        if(ProtocalStrategyCache.protocalServerCache.containsKey(pid)){
            //do nothing
        }else{
            //start server
            if(!isAppoitedParseMethod(pid)){
                String pts = ProtocalStrategyCache.protocalStrategyCache.get(pid);
                System.out.println("启动规约="+pts);
                new Thread(new Runnable() {
                    public void run() {

                        String[] pt = pts.split("\\,");
                        boolean isBigEndian = "0".equals(pt[1]) ? false : true;
                        boolean isDataLenthIncludeLenthFieldLenth = "0".equals(pt[5]) ? false : true;
                        System.out.println(String.format("！！！网关开始提供规约类型为%s的终端连接服务，开启端口号为：%s", Integer.parseInt(pt[0]),Integer.parseInt(pt[7])));
                        Server2Terminal server2Terminal = new Server2Terminal(pt[0],pt[7]);
                        server2Terminal.bindAddress(server2Terminal.config(Integer.parseInt(pt[0]),isBigEndian,Integer.parseInt(pt[2]),
                                Integer.parseInt(pt[3]),Integer.parseInt(pt[4]),isDataLenthIncludeLenthFieldLenth,Integer.parseInt(pt[6])));//1, false, -1, 1, 2, true, 1

                    }
                },"gate2tmnlThread_pid_"+pid).start();
            }else{
                //TODO 高级功能模块   自定义解析器实现
            }
        }
        return responseData;
    }

    @Override
    public ResponseData stopProtocalServiceByPid(String pid) {
        ResponseData responseData = new ResponseData();
        if(pid == null|| "".equals(pid) ){
            responseData.setReturnCode(500);
            responseData.setErroInfo(new IllegalArgumentException("参数不正确,请检查参数设置！"));
            return responseData;
        }
        Server2Terminal server2Terminal = ProtocalStrategyCache.protocalServerCache.get(pid);
        if(server2Terminal != null){
            server2Terminal.close();
        }
        return responseData;
    }

    /**
     * 获取所有规约
     * @param onlyRunning
     * @return
     */
    @Override
    public ResponseData getAllProtocal(boolean onlyRunning) {
        ResponseData responseData = new ResponseData();
        List<Object> list = new ArrayList<>();

        if(onlyRunning){
            //查询正在运行解析服务的规约
            Map<String,String> data = new HashMap<>();
            Set set = ProtocalStrategyCache.protocalStrategyCache.keySet();
            for (Object pid : set) {
                if(ProtocalStrategyCache.protocalServerCache.containsKey(pid)){
                    data.put(pid.toString(), ProtocalStrategyCache.protocalStrategyCache.get(pid));
                }
            }
            list.add(data);
        }else{
            list.add(ProtocalStrategyCache.protocalStrategyCache);
        }
        responseData.setData(list);
        return responseData;
    }


    /**
     * 新增加规约
     * @param pid
     * @param str 共8位 =pId+ isBigEndian+ beginHexVal+ lengthFieldOffset+ lengthFieldLength+ isDataLenthIncludeLenthFieldLenth+ exceptDataLenth+ port
     * @param startAtOnce  是否立即启动服务
     * @return
     */
    @Override
    public ResponseData addNewProtocal(String pid,List<Integer> str,boolean startAtOnce) {
        ResponseData responseData = new ResponseData();
        if(pid == null|| "".equals(pid) || str == null || str.isEmpty() ){
            responseData.setReturnCode(500);
            responseData.setErroInfo(new IllegalArgumentException("参数不正确,请检查参数设置！"));
            return responseData;
        }
        if(ProtocalStrategyCache.protocalStrategyCache.containsKey(pid)){
            //exist
            return responseData;
        }else{
            //not exist
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.size(); i++) {
                sb.append(str.get(i)+",");
            }
            String newStraPro = sb.toString();
            ProtocalStrategyCache.protocalStrategyCache.put(pid, newStraPro.substring(0, newStraPro.length()-1));
        }
        if(startAtOnce){
            return startProtocalServiceByPid(pid);
        }
        return responseData;
    }
    /**
     * 判断当前规约是否指定了自定义长度域解析方法
     * @param pid
     * @return 存在自定义解析方法则返回true
     */
    private boolean isAppoitedParseMethod(String pid){
        String str = ProtocalStrategyCache.protocalStrategyClassUrlCache.get(pid);
        return str != null;
    }

}
