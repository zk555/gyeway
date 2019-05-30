package com.gy.gyeway;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;

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
    @Test
    public static void contextLoads1() {
        Stu s = new Stu();
        s.setName("yc");
        s.setClazz(new Class<?>[]{String.class});
        String str = JSON.toJSONString(s);
        System.out.println("str"+str);

        Stu stu = JSON.parseObject(str, Stu.class);
        System.out.println(stu.getName());
    }





}

class Stu implements Serializable {
    private static final long serialVersionUID = 4155716943529879886L;

    String name;
    Class<?>[] clazz ;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Class<?>[] getClazz() {
        return clazz;
    }
    public void setClazz(Class<?>[] clazz) {
        this.clazz = clazz;
    }

}
