package com.gy.gyeway.rpc.rpcService;

import com.gy.gyeway.base.cache.ProtocalStrategyCache;
import com.gy.gyeway.rpc.annotation.RPCService;
import com.gy.gyeway.rpc.dataBridge.ResponseData;
import com.gy.gyeway.server.Server2Terminal;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.ArrayList;
import java.util.List;
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
//		stopProtocalServiceByPid(pid);
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//3.启动服务
//		startProtocalServiceByPid(pid);
        return responseData;
    }

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

    @Override
    public ResponseData startProtocalServiceByPid(String pid) {
        ResponseData responseData = new ResponseData();
        if(pid == null|| "".equals(pid) ){
            responseData.setReturnCode(500);
            responseData.setErroInfo(new IllegalArgumentException("参数不正确,请检查参数设置！"));
            return responseData;
        }
        if(ProtocalStrategyCache.protocalServerCache.contains(pid)){
            //do nothing
        }else{
            //start server
            String pts = ProtocalStrategyCache.protocalStrategyCache.get(pid);
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
            ProtocalStrategyCache.protocalStrategyCache.put(pid, pts);
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

    @Override
    public ResponseData getAllProtocal() {
        ResponseData responseData = new ResponseData();
        List<Object> list = new ArrayList<>();
        list.add(ProtocalStrategyCache.protocalStrategyCache);
        responseData.setData(list);
        return responseData;
    }


    @Override
    public ResponseData addNewProtocal(String pid,List<Integer> str,boolean startAtOnce) {
        ResponseData responseData = new ResponseData();
        if(pid == null|| "".equals(pid) || str == null || str.isEmpty() ){
            responseData.setReturnCode(500);
            responseData.setErroInfo(new IllegalArgumentException("参数不正确,请检查参数设置！"));
            return responseData;
        }
        if(ProtocalStrategyCache.protocalStrategyCache.contains(pid)){
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

//    @Override
//    public ResponseData addStrategyByAppointParseMethod(String pid,List<Integer> strategy ,String Content) {
//        ResponseData responseData = new ResponseData();
//        //BasicDir
//        String className = null;
//        try {
//            className = makeClass(pid , Content);
//        } catch (Exception e) {
//            e.printStackTrace();
//            responseData.setErroInfo(e);
//            responseData.setReturnCode(500);
//            return responseData;
//        }
//        if(className != null){
//            ProtocalStrategyCache.protocalStrategyClassUrlCache.put(pid, className);
//            addNewProtocal(pid,strategy,false);
//        }
//        return responseData;
//    }


    /**
     * 判断当前规约是否指定了自定义长度域解析方法
     * @param pid
     * @return 存在自定义解析方法则返回true
     */
    private boolean isAppoitedParseMethod(String pid){
        String str = ProtocalStrategyCache.protocalStrategyClassUrlCache.get(pid);
        return str != null;
    }


    /**
     * 创建自定义对象
     * @return 全类名
     * @throws Exception
     */
    public static String makeClass(String pid , String methodContent) throws Exception{
        ClassPool pool = ClassPool.getDefault();
        String newClassName = "iotGate.strategy.Strategy"+pid;
        CtClass clazz=pool.get("gate.codec.other.LengthParserImpl");
        clazz.setName(newClassName);

        CtMethod method = clazz.getDeclaredMethod("parseLength", new CtClass[]{pool.get("io.netty.buffer.ByteBuf"),pool.get("java.util.ArrayList")});
        //在方法体之前增加输出
        method.insertAfter("System.out.println(\"start....\"); "+ methodContent );

        clazz.writeFile(System.getProperty("BasicDir"));
        return newClassName;
    }
}
