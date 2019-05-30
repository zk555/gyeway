package com.gy.gyeway.codec;

import com.gy.gyeway.rpc.dataBridge.ResponseData;
import com.gy.gyeway.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<ResponseData> {

@Override
protected void encode(ChannelHandlerContext ctx, ResponseData msg, ByteBuf out) throws Exception {
    byte[] data = SerializationUtil.serialize(msg);
    out.writeShort(data.length);
    out.writeBytes(data);

    }
}
