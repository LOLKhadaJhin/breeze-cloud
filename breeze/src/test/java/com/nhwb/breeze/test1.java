package com.nhwb.breeze;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class test1 {
    @Test
    public void t(){
        String text = "E:\\演示\\其他1\\仙剑奇侠传奥术大师多所asdasdadasdasddsadassa大所大所大所大所大大所大所多撒大所1";
        String md5 = DigestUtils.md5DigestAsHex(text.getBytes(StandardCharsets.UTF_8));
        String uu = UUID.nameUUIDFromBytes(text.getBytes()).toString();
        System.out.println(md5);
        System.out.println(md5.length());
        System.out.println(uu);
        System.out.println(uu.length());
    }
}
