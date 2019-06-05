package com.gy.gyeway.test.moniMaster;

import com.gy.gyeway.base.constant.ConstantValue;
import com.gy.gyeway.base.domain.ChannelData;
import com.gy.gyeway.base.domain.SocketData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器，模拟前置解码器
 * 
 * @author BriansPC
 *
 */
public class moniMasterDecoder  extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//解码网关头 获取终端ip
		ChannelData channelData = decodeGateHeader(in);
		if(channelData != null){
			out.add(channelData);
		}
	}
	public ChannelData decodeGateHeader(ByteBuf in){
		if(in.readableBytes()>31){
			//网关头固定为28位  加SocketData至少3位
			StringBuilder clientIpAddress ;
			int beginReader;
			while (true) {
				beginReader = in.readerIndex();
				int gateHeader = in.readByte() & 0xFF;
				if(gateHeader == ConstantValue.GATE_HEAD_DATA){
					//1.获取到网关头A8
					int socketDataLen =  readLenArea(in);//in.readShortLE();//
					if(in.readableBytes() >= (socketDataLen+25) ){
						in.readerIndex(beginReader);
						SocketData data = new SocketData(in.readBytes(socketDataLen+28));
						ChannelData channelData =  new ChannelData(data);
						return channelData;
					}else{
						//报文不完整
						in.readerIndex(beginReader);
						break;
					}
				}else{
					if (in.readableBytes() <= 31) {
						return null;
					}
					continue ;
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

}
