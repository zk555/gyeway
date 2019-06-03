package com.gy.gyeway.client.handler;

import com.gy.gyeway.base.cache.ClientChannelCache;
import com.gy.gyeway.base.cachequeue.CacheQueue;
import com.gy.gyeway.base.domain.ChannelData;
import com.gy.gyeway.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Client2MasterInHandler  extends SimpleChannelInboundHandler<ChannelData> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChannelData msg) throws Exception {
//        String str=  StringUtils.encodeHex(msg.getSocketData().getLenArea())+StringUtils.encodeHex(msg.getSocketData().getContent());


        Channel channel = ClientChannelCache.get(msg.getIpAddress());//127:0:0:1:56445
//		System.out.println("下行时得到终端ip=="+msg.getIpAddress());
        if(channel != null){
            int len = msg.getSocketData().getByteBuf().readableBytes();
            byte[] car =  new byte[len];
            msg.getSocketData().getByteBuf().readBytes(car);
            msg.getSocketData().getByteBuf().readerIndex(0);
//			System.out.println("Gate Down = "+StringUtils.encodeHex(car));
            channel.writeAndFlush(msg);
//            System.out.println("Gate Down = 68"+str+"16");//3000010523605413040000A4D781008007E20802050F250D000D07E20802050F250D000D07E20802050F250D000D56F1
        }


    }
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
