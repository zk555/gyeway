package com.gy.gyeway.test.moniTerminal;

import com.gy.gyeway.test.CountHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端处理服务端返回信息的处理器
 */
public class MoniTerminalHandler  extends SimpleChannelInboundHandler<ByteBuf> {

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("模拟终端与网关通道建立。。。。。。");
        System.out.println("模拟终端本地ip："+ctx.channel().localAddress());
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO Auto-generated method stub
        System.err.println("出现异常。。。");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        // TODO Auto-generated method stub
        ByteBuf recieveMsg=(ByteBuf) msg;
        String code = ByteBufUtil.hexDump(recieveMsg).toUpperCase();//将bytebuf中的可读字节 转换成16进制数字符串

        System.out.println("接收总数："+CountHelper.clientRecieveCount.addAndGet(1)+" ;模拟终端收到数据w："+code);
    }

}
