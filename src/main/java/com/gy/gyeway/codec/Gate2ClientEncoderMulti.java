package com.gy.gyeway.codec;

import com.gy.gyeway.base.domain.ChannelData;
import com.gy.gyeway.base.domain.SocketData;
import com.gy.gyeway.utils.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器 将对象 编码成字节数组  --->目的地是终端硬件
 */
public class Gate2ClientEncoderMulti extends MessageToByteEncoder<ChannelData>{

    @Override
    protected void encode(ChannelHandlerContext ctx, ChannelData msg, ByteBuf out) throws Exception {
        SocketData data = msg.getSocketData();

        out.writeBytes(data.getByteBuf());
        CommonUtil.releaseByteBuf(data.getByteBuf());
    }


}
