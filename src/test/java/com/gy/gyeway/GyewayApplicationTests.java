package com.gy.gyeway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GyewayApplicationTests {

    @Test
    public void contextLoads() {
        //		int i =  12 & 0xFF << 8 ;
//		System.out.println(i);
        byte header = 127;//byte有正负，1位标识位，7位数据位，所以最大值为2^7 - 1 = 127
        byte header2 = -128;
        System.out.println(header&0xFF);
        System.out.println(header2&0xFF);
    }
//
//    @Test
//    public static final int decodeHex(String hex, byte[] bytes) {
//        int byteCount = 0;
//        int length = hex.length();
//        for (int i = 0; i < length; i += 2) {
//            byte newByte = 0;
//            newByte = (byte) (newByte | hexCharCodes[hex.charAt(i)]);
//            newByte = (byte) (newByte << 4);
//            newByte = (byte) (newByte | hexCharCodes[hex.charAt(i + 1)]);
//            bytes[byteCount] = newByte;
//            byteCount++;
//        }
//
//        return byteCount;
//    }

}
