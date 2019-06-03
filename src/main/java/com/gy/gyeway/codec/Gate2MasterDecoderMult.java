package com.gy.gyeway.codec;

import com.gy.gyeway.base.constant.ConstantValue;
import com.gy.gyeway.base.domain.ChannelData;
import com.gy.gyeway.base.domain.SocketData;
import com.gy.gyeway.utils.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.List;

/**
 * 解码器，，将字节形式的报文 解码成对象
 */
public class Gate2MasterDecoderMult extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

//        ByteBuf tmnlDataBuf = CommonUtil.getDirectByteBuf();
        //解码网关头 获取终端ip
        ChannelData channelData = decodeGateHeader(in);
        if(channelData != null){
            out.add(channelData);

        }

    }


    public ChannelData decodeGateHeader(ByteBuf in) {
        if (in.readableBytes() > 31) {
            //网关头固定为28位  加SocketData至少3位
            StringBuilder clientIpAddress;
            int beginReader;

            while (true) {
                beginReader = in.readerIndex();
                int gateHeader = in.readByte() & 0xFF;
                if (gateHeader == ConstantValue.GATE_HEAD_DATA) {
                    //1.获取到网关头A8
                    int socketDataLen = in.readShortLE();// readLenArea(in);
                    if (in.readableBytes() >= (socketDataLen + 25)) {
                        //报文完整
                        in.skipBytes(1);
                        boolean isIPV4 = true;
                        int pId = -1;
                        {
                            int sig = in.readByte() & 0xFF;
                            pId = sig & 127;
                            int type = sig >> 7 & 1;
                            isIPV4 = type == 0 ? true : false;

                        }
                        clientIpAddress = new StringBuilder();
                        if (isIPV4) {
                            in.skipBytes(13);
                            clientIpAddress.append(in.readByte() & 0xFF);  //ip地址需要转成10进制数
                            clientIpAddress.append(".");
                            clientIpAddress.append(in.readByte() & 0xFF);
                            clientIpAddress.append(".");
                            clientIpAddress.append(in.readByte() & 0xFF);
                            clientIpAddress.append(".");
                            clientIpAddress.append(in.readByte() & 0xFF);


                        } else {
                            in.skipBytes(1);
                            byte[] dataTemp = new byte[16];
                            for (int i = 0; i < 16; i++) {
                                dataTemp[i] = in.readByte();
                            }
                            try {
                                clientIpAddress.append(Inet6Address.getByAddress(dataTemp).getHostAddress());
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                        clientIpAddress.append("|");
                        clientIpAddress.append(readLenArea(in));

                        in.skipBytes(4);//连接次数
                        SocketData data = new SocketData(in.readBytes(socketDataLen));
                        data.setpId(pId);//规约类型
                        ChannelData channelData = new ChannelData(data);
                        channelData.setIpAddress(clientIpAddress.toString());


                        return channelData;
                    } else {
                        //报文不完整
                        in.readerIndex(beginReader);
                        break;
                    }
                } else {
                    if (in.readableBytes() <= 31) {

                        return null;
                    }
                    continue;
                }
            }
        }

        return null;
    }
    /**
     * ByteBuf获取读指针后两个字节的数据，并计算对应长度值并返回---小端模式
     * @param in byteBuf
     * @return
     */
    public int readLenArea(ByteBuf in){

        ByteBuf buf = in.readBytes(2);//两个字节的长度域
//		lenArea = buf.array();//不能使用.array  因为默认是零拷贝
        byte left = buf.readByte();
        byte right = buf.readByte();
        int count = (left & 0xFF) + ((right & 0xFF) << 8 );
        return count;
    }
    /**
     * 获取报文长度，并且获取报文长度大小的byte[]  正常获取返回长度int值
     * @param in byteBuf
     * @param lenArea 存储长度域2个字节的数据
     * @return
     */
    public int readLenArea(ByteBuf in,byte[] lenArea){

        ByteBuf buf = in.readBytes(2);//两个字节的长度域
//		lenArea = buf.array();//不能使用.array  因为默认是零拷贝
        byte left = buf.readByte();
        byte right = buf.readByte();
        lenArea[0] = left;
        lenArea[1] = right;
        int count = (left & 0xFF) + ((right & 0xFF) << 8 );
        return count;
    }
    /**
     * 获取报文的结束标识  正常获取返回结果
     * @param in byteBuf
     * @param len 读取区间
     * @return
     */
    public byte[] readContent(ByteBuf in, int len){
        byte[] bs = new byte[len];
        if(in.readableBytes()>len){

            ByteBuf buf= in.readBytes(len);
            buf.getBytes(0, bs);
            return bs;
        }
        return null;
    }

    /**
     * 获取报文的结束标识  正常获取返回true
     * @param in byteBuf
     * @return boolean
     */
    public boolean isEnd(ByteBuf in){

        return in.readByte() == ConstantValue.END_DATA;
    }


}
