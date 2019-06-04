package com.gy.gyeway.codec.other;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

public interface LengthParser {
    /**
     *
     * @param byteBuf 数据来源
     * @param rets 数据返回封装
     */
    void parseLength(ByteBuf byteBuf, ArrayList<Integer> rets);
}
