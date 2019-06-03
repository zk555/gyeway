package com.gy.gyeway.server;

import com.gy.gyeway.codec.Gate2ClientDecoderMulti;
import com.gy.gyeway.codec.Gate2ClientEncoderMulti;
import com.gy.gyeway.server.handler.SocketInHandler;
import com.gy.gyeway.utils.CommonUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 网关获取报文
 */
public class Server2Terminal {
    private static EventLoopGroup boss = new NioEventLoopGroup(1);
    private static  EventLoopGroup work = new NioEventLoopGroup();
    /**
     * 通过引导配置参数
     * @return
     */
    public static ServerBootstrap config(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //心跳检测,超时时间300秒，指定时间中没有读写操作会触发IdleStateEvent事件
                        ch.pipeline().addLast(new IdleStateHandler(0, 0, 300, TimeUnit.SECONDS));
                        //自定义编解码器  需要在自定义的handler的前面即pipeline链的前端,不能放在自定义handler后面，否则不起作用
                        ch.pipeline().addLast("decoder",new Gate2ClientDecoderMulti(1, false, 1024, 1, 2, true, 1));//698长度域表示不包含起始符和结束符长度
//				ch.pipeline().addLast("decoder",new Gate2ClientDecoder());//698长度域表示不包含起始符和结束符长度
                        ch.pipeline().addLast("encoder",new Gate2ClientEncoderMulti());
                        ch.pipeline().addLast(new SocketInHandler());
                    }
                });

        return serverBootstrap;


    }
    /**
     * 绑定服务到指定端口
     * @param serverBootstrap
     */
    public static void bindAddress(ServerBootstrap serverBootstrap,int address){
        ChannelFuture channelFuture;
        try {
            channelFuture = serverBootstrap.bind(address).sync();
            System.out.println("网关服务端已启动！！");
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {

            e.printStackTrace();
        }finally{
            CommonUtil.closeEventLoop(boss,work);
        }
    }
}
