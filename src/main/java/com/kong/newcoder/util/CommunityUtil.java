package com.kong.newcoder.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author shijiu
 */

public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //MD5加密
    public static String md5(String key){
        //如果为空
        if(StringUtils.isBlank(key)){
            return null;
        }
        //md5加密
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
