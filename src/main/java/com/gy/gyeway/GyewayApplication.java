package com.gy.gyeway;

import com.gy.gyeway.base.cache.ClientChannelCache;
import com.gy.gyeway.base.cache.ProtocalStrategyCache;
import com.gy.gyeway.base.cachequeue.CacheQueue;
import com.gy.gyeway.base.cluster.ZKFramework;
import com.gy.gyeway.client.Client2Master;
import com.gy.gyeway.rpc.RPCProcessor.RPCProcessor;
import com.gy.gyeway.rpc.RPCProcessor.RPCProcessorImpl;
import com.gy.gyeway.server.Server2Terminal;
import com.gy.gyeway.threadWorkers.MClient2Tmnl;
import com.gy.gyeway.threadWorkers.TServer2MClient;
import com.gy.gyeway.utils.CommonUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class GyewayApplication {
    public static CommandLine commandLine = null;
    public static int gatePort = 9811;
    public static String zkAddr = null;
    public static List<String> masterAddrs = new ArrayList<>(1);
    public static CountDownLatch locks = new CountDownLatch(1);
    private static RPCProcessor processor = new RPCProcessorImpl();
    private static String[] protocolType;
    public static void main(String[] args) {
        boolean isCluster = suitCommonLine(args);
        initEnvriment();
        System.setProperty("org.jboss.netty.epollBugWorkaround", "true");
        if(isCluster){
            try {
                locks.await();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }else{
            //启动与前置对接的客户端  因为是阻塞运行 需要开线程启动
            for(int i = 0 ; i < masterAddrs.size() ; i++){
                String addr = masterAddrs.get(i);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Client2Master client2Master = new Client2Master();
                            client2Master.bindAddress2Client(client2Master.configClient(),addr,8888);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },"gate2masterThread_ip_"+addr).start();
            }
        }
        //启动与终端对接的服务端  因为是阻塞运行 需要开线程启动---后续版本中会变动
        for(int i = 0 ; i < protocolType.length ; i++){
            //启动与终端对接的服务端  因为是阻塞运行 需要开线程启动---后续版本中会变动
            String pts =  protocolType[i];
            String pid = pts.split("\\,")[0];//pId
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
        try {
            processor.exportService();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addHook();
    }


    /**
     * 命令行
     */
    public static boolean suitCommonLine(String[] args){
        commandLine =
                CommonUtil.parseCmdLine("iotGateServer", args, CommonUtil.buildCommandlineOptions(new Options()),
                        new PosixParser());
        if (null == commandLine) {
            System.exit(-1);
        }
        boolean isCluster = false;
        if(commandLine.hasOption("c") && commandLine.hasOption("z")){
            isCluster = true;
            zkAddr = commandLine.getOptionValue("z");
            new ZKFramework().start(zkAddr);
        }else if (commandLine.hasOption("m")) {
            String[] vals =  commandLine.getOptionValue("m").split("\\,");
            for (String string : vals) {
                masterAddrs.add(string);
            }
        }else{
            System.err.println("启动参数有误，请重新启动");
            System.exit(-1);
        }
        String confFile = commandLine.getOptionValue("f");
        protocolType = getProtocolType(confFile);
        CommonUtil.gateNum = Integer.parseInt(commandLine.getOptionValue("n"));
        System.out.println(String.format("网关编号为：%s", CommonUtil.gateNum));
        if(commandLine.hasOption("p")){
            gatePort = Integer.parseInt(commandLine.getOptionValue("p"));
        }
        return isCluster;
    }
    /**
     * 环境初始化  ---目前最还先不用spring管理
     */
    public static  void initEnvriment(){
        //初始化数据中转线程
        try {
            new TServer2MClient(CacheQueue.up2MasterQueue,1).start();
            new MClient2Tmnl(CacheQueue.down2TmnlQueue, 1).start();
        } catch (Exception e) {
            System.err.println("数据中转线程启动失败");
            e.printStackTrace();
            System.exit(-1);
        };

    }
    /**
     * JVM的关闭钩子--JVM正常关闭才会执行
     */
    public static void addHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                //清空缓存信息
                System.out.println("网关正常关闭前执行  清空所有缓存信息...............................");
                ClientChannelCache.clearAll();
                CacheQueue.clearIpCountRelationCache();
                CacheQueue.clearMasterChannelCache();
            }
        }));
    }

    @SuppressWarnings("resource")
    public static String[] getProtocolType(String filePath){
        File conf=  new File(filePath);
        System.setProperty("BasicDir",conf.getParent() );
        BufferedReader bufferedReader =null;
        try {
            bufferedReader = new BufferedReader(new FileReader(conf));
            String str;
            while((str = bufferedReader.readLine()) != null){
                if(str.startsWith("protocolType")){
                    return str.split("\\=")[1].split(";");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("配置文件加载失败");
            System.exit(-1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
