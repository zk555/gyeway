package com.gy.gyeway.codec;

import com.gy.gyeway.rpc.RPCProcessor.RPCProcessor;
import com.gy.gyeway.rpc.RPCProcessor.RPCProcessorImpl;
import com.gy.gyeway.rpc.dataBridge.RequestData;
import com.gy.gyeway.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;

public class RpcDecoder  extends LengthFieldBasedFrameDecoder {

    public RpcDecoder() {
        super(10240, 0, 2, 0, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf buff =  (ByteBuf) super.decode(ctx, in);
        if(buff == null){
            return null;
        }
        ByteBuffer byteBuffer = buff.nioBuffer();
        int dataAllLen = byteBuffer.limit();
        int lenArea = byteBuffer.getShort();
        int dataLen = dataAllLen - 2;
        byte[] contentData = new byte[dataLen];
        byteBuffer.get(contentData);//报头数据
        RequestData requestData = SerializationUtil.deserialize(contentData, RequestData.class);
        return requestData;
    }


}
