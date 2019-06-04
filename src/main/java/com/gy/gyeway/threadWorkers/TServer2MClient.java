package com.gy.gyeway.threadWorkers;

import com.gy.gyeway.base.cachequeue.CacheQueue;
import com.gy.gyeway.base.domain.ChannelData;
import com.gy.gyeway.base.domain.SocketData;
import com.gy.gyeway.concurrent.ThreadFactoryImpl;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * * 通过up2MasterQueue对列中获取从Server4Terminal发送过来的上行报文对象，
 *  * 并通过缓存的master的channel发送出去
 */
public class TServer2MClient  implements DataTransfer{

    private BlockingQueue<ChannelData> up2MasterQueue;
    private final int poolSize;
    ExecutorService exService;
    public TServer2MClient(BlockingQueue<ChannelData> up2MasterQueue ,int poolSize) {
        super();
        this.up2MasterQueue = up2MasterQueue;
        this.poolSize = poolSize;
        exService = Executors.newFixedThreadPool(poolSize,new ThreadFactoryImpl("msgTransWorker_UP_", false));
    }


    /**
     * up2MasterQueue 中获取终端发送的报文 ，处理后再通过缓存Channel写出去
     */
    public void run() {
        for (int i=0 ; i < poolSize ; i++ ){
            exService.execute(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        ChannelData channelData = null;
                        try {
                            channelData = up2MasterQueue.take();//获取从Server4Terminal发送过来的上行报文对象
                            if(channelData == null){
                                continue;
                            }
                            //获取前置与网关连接channel
                            Channel masterChannel = CacheQueue.choiceMasterChannel();
                            /**
                             * 1.通过channel发送数据--会执行编码器等handler
                             * 2.发送数据前判断masterChannel是否可写，因为配置了高水位和低水位，有可能channel为“不可写”状态
                             */
                            if(masterChannel != null  &&  masterChannel.isWritable()){
                                masterChannel.writeAndFlush(channelData);
                            }else{
                                System.out.println("masterChannel为空或者masterChannel不可写");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

    }



    @Override
    public void start() throws Exception {
        new Thread(this).start();
    }


}
