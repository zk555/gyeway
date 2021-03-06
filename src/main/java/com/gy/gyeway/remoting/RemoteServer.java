package com.gy.gyeway.remoting;

import com.gy.gyeway.codec.RpcDecoder;
import com.gy.gyeway.codec.RpcEncoder;
import com.gy.gyeway.concurrent.ThreadFactoryImpl;
import com.gy.gyeway.rpc.RPCProcessor.RPCProcessor;
import com.gy.gyeway.rpc.RPCProcessor.RPCProcessorImpl;
import com.gy.gyeway.rpc.dataBridge.RequestData;
import com.gy.gyeway.rpc.dataBridge.ResponseData;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

/**
 * rpc远程服务handler ,通用通讯服务端
 */
public class RemoteServer {
    private final ServerBootstrap bootstrap;
    private final EventLoopGroup eventLoopGroupWorker;
    private final EventLoopGroup eventLoopGroupBoss;
    RPCProcessor processor = new RPCProcessorImpl();
    public RemoteServer() {

        bootstrap = new ServerBootstrap();
        eventLoopGroupBoss = new NioEventLoopGroup(1);
        eventLoopGroupWorker = new NioEventLoopGroup(2,new ThreadFactoryImpl("netty_RPC_selecter_", false));
    }

    public void start(){
        bootstrap.group(eventLoopGroupBoss, eventLoopGroupWorker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.SO_RCVBUF, 65535)
                .localAddress(new InetSocketAddress(10916))//默认端口10916
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new RpcEncoder(), //
                                new RpcDecoder(), //
                                new IdleStateHandler(0, 0, 120),//
                                new NettyConnetManageHandler(), //
                                new NettyServerHandler());
                    }
                });


        try {
            ChannelFuture sync = this.bootstrap.bind(10916).sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();
//		this.port = addr.getPort();
        }
        catch (InterruptedException e1) {
            throw new RuntimeException("this.bootstrap.bind().sync() InterruptedException", e1);
        }
    }


    class NettyConnetManageHandler extends ChannelDuplexHandler {
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            super.channelRegistered(ctx);
        }


        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);

        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent evnet = (IdleStateEvent) evt;
                if (evnet.state().equals(IdleState.ALL_IDLE)) {
                    ctx.channel().close();
                }
            }
            ctx.fireUserEventTriggered(evt);
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.channel().close();
        }
    }
    /**
     * class_name: RemoteServer
     * package: com.gy.gyeway.remoting
     * describe: TODO :调用本地方法，然后写回
     * creat_user: zhaokai@
     * creat_date: 2019/5/30
     * creat_time: 18:57
     **/
    class NettyServerHandler extends SimpleChannelInboundHandler<RequestData> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RequestData msg) throws Exception {
            /**
             * 调用本地方法
             */
            ResponseData rsp = processor.executeService(msg);
            ctx.channel().writeAndFlush(rsp);
        }
    }

}
