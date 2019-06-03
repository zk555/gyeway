package com.gy.gyeway.client;

import com.gy.gyeway.base.cache.Cli2MasterLocalCache;
import com.gy.gyeway.base.constant.ConstantValue;
import com.gy.gyeway.base.domain.GateHeader;
import com.gy.gyeway.client.handler.Client2MasterInHandler;
import com.gy.gyeway.codec.Gate2MasterDecoderMult;
import com.gy.gyeway.codec.Gate2MasterEncoderMult;
import com.gy.gyeway.utils.CommonUtil;
import com.gy.gyeway.utils.StringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.Inet4Address;
import java.net.InetSocketAddress;

/**
 * class_name: Client2Master
 * package: com.gy.gyeway.client
 * describe: 网关与前置相连的客户端
 * creat_user: zhaokai@
 * creat_date: 2019/5/23
 * creat_time: 15:56
 **/
public class Client2Master {

    private  EventLoopGroup worker = new NioEventLoopGroup();
    private Cli2MasterLocalCache cli2MasterLocalCache = Cli2MasterLocalCache.getInstance();
    private String ip;
    public  Bootstrap configClient(){
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                /**
                 * 设置ByteBuf的高低水位线，原方法WRITE_BUFFER_HIGH_WATER_MARK，WRITE_BUFFER_LOW_WATER_MARK已经被废弃
                 * 由WRITE_BUFFER_WATER_MARK代替
                 */
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024 * 1024, 64 * 1024 * 1024))//加上该配置后，网byteBuf写数据前需要判断iswriteble
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //添加控制链
                        ch.pipeline().addLast(new Gate2MasterDecoderMult());
                        ch.pipeline().addLast(new Gate2MasterEncoderMult());//自定义编解码器
                        ch.pipeline().addLast(new Client2MasterInHandler());
                    }

                });
        return bootstrap;
    }
    /**
     * 绑定客户端到指定的ip 和 端口port
     * @param bootstrap
     * @param ip
     * @param port
     * @throws Exception
     */
    public  void bindAddress2Client(Bootstrap bootstrap,String ip, int port) throws Exception{
        cli2MasterLocalCache.set(ip, this);
        this.ip = ip;
        ChannelFuture channelFuture=bootstrap.connect(ip, port).sync();

        /**
         * 链接成功之后 向前置发送网关头信息
         */
//		String msg = "A80000030F010000000000000000000000007F00000150FE01000000";
        //			  A800000100010000000000000000000000007F000001C7F901000000
//		byte[] byteMsg = StringUtils.decodeHex(msg);
        Channel channel  =  channelFuture.channel();
        //获取网关本地地址
        InetSocketAddress insocket = (InetSocketAddress)channel.remoteAddress();
        String ipAddress = StringUtils.formatIpAddress(insocket.getHostName(), String.valueOf(insocket.getPort()));
        ByteBuf buf = loginGateHeader(ipAddress);
        channelFuture.channel().writeAndFlush(buf);
        channelFuture.channel().closeFuture().sync();
    }

    /**
     * 组装网关登录报文
     * @param channel
     * @throws Exception
     */
    public  ByteBuf loginGateHeader(String LocalIpAddress) throws Exception{
        /**
         * 创建直接内存形式的ByteBuf，不能使用array()方法，但效率高
         */
        /**
         * 创建直接内存形式的ByteBuf，不能使用array()方法，但效率高
         */
        ByteBuf out = CommonUtil.getDirectByteBuf();
//		PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
//		ByteBuf out = allocator.buffer();
        String ipAddress = LocalIpAddress;
        //连接序号默认1
        int count = 1;
        //登录报文的真实报文长度为0（真实报文是指 以68开始 16结尾的报文）
        int len = 0;


        GateHeader headBuf= new GateHeader();
        headBuf.writeInt8(Integer.valueOf(ConstantValue.GATE_HEAD_DATA).byteValue());
        headBuf.writeInt16(len);//整个长度
        headBuf.writeInt8(Integer.valueOf("03").byteValue());//type
        headBuf.writeInt8(Integer.valueOf("15").byteValue());//protocolType
        headBuf.writeInt8((byte) CommonUtil.gateNum);//网关编号
        for(int i = 0; i < 3; i++) {  //12个字节的00
            headBuf.writeInt32(0);
        }

        byte[] bs = Inet4Address.getByName(ipAddress.split("|")[0]).getAddress();//127.0.0.1 -->  [127, 0, 0, 1]
        headBuf.writeInt8(bs[0]);
        headBuf.writeInt8(bs[1]);
        headBuf.writeInt8(bs[2]);
        headBuf.writeInt8(bs[3]);
        headBuf.writeInt16(Integer.parseInt(ipAddress.split("|")[1]));//port  两个字节表示端口号
        headBuf.writeInt32(count);//count  4个字节的count
        out.writeBytes(headBuf.getDataBuffer());
        return out;
    }

    /**
     * 关闭服务
     */
    public void close(){
        CommonUtil.closeEventLoop(worker);
        cli2MasterLocalCache.del(ip);
    }

}
