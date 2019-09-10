package com.suiyueyule.webchat.constance;

/**
 * @Author: cityuu#163.com
 * @Date: 2019-08-29 11:30
 * @version: v1.0
 * @Description:
 */
public class CacheKeys {

    public static final String DOMAIN = "webchat";


    /**
     * 获取单个用户1对1会话的Key
     * @param code
     * @return
     */
    public static final String getChatMapKey(String code){
        return  DOMAIN+":p2p:"+code;
    }
}
