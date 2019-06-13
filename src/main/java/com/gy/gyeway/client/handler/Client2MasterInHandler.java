package com.gy.gyeway.client.handler;

import com.gy.gyeway.base.cachequeue.CacheQueue;
import com.gy.gyeway.base.domain.ChannelData;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

@ChannelHandler.Sharable
public class Client2MasterInHandler  extends SimpleChannelInboundHandler<ChannelData> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChannelData msg) throws Exception {
        if(msg instanceof List){
            List<ChannelData> dataList = (List<ChannelData>) msg;
            for (ChannelData channelData : dataList) {
                CacheQueue.down2TmnlQueue.put(channelData);
            }
        }else{
            ChannelData channelData = (ChannelData)msg;
            CacheQueue.down2TmnlQueue.put(channelData);
        }
    }

    /**
     * 缓存
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        /**
         * 一旦网关与前置 建立连接 将  该连接通道channel缓存起来，方便Server选择发送上行报文的前置
         */
        String masterIP = ctx.channel().remoteAddress().toString().replaceAll("\\/", "");
        CacheQueue.addMasterChannel2LocalCache(masterIP, ctx.channel());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        /**
         * 当网关与前置断开连接 则从缓存中删除对应的channel 以便选择存活的channel发送报文到前置
         */
        String masterIP = ctx.channel().remoteAddress().toString().replaceAll("\\/", "");;

        CacheQueue.removeMasterChannelFromLocalCache(masterIP);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }


}
