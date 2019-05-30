package com.gy.gyeway.codec;

import com.gy.gyeway.rpc.RPCProcessor.RPCProcessor;
import com.gy.gyeway.rpc.RPCProcessor.RPCProcessorImpl;
import com.gy.gyeway.rpc.dataBridge.RequestData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;

public class RpcDecoder  extends LengthFieldBasedFrameDecoder {

    RPCProcessor processor = new RPCProcessorImpl();

    public RpcDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
                      int initialBytesToStrip) {
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
        int dataLen = dataAllLen - lenArea;
        byte[] contentData = new byte[dataLen];
        byteBuffer.get(contentData);//报头数据
        RequestData requestData = gate.util.MixAll.decode(contentData, RequestData.class);
        processor.executeService(requestData);
        return null;
    }


}
