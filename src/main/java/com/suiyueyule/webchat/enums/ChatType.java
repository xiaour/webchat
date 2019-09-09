package com.suiyueyule.webchat.enums;

/**
 * @Author: zhangtao@suiyueyule.com
 * @Date: 2019-08-29 11:55
 * @version: v1.0
 * @Description:  会话类型
 */
public enum ChatType {

    SYSTEM_CHAT,//系统会话，不可展示
    USER_CHAT,//用户会话
    NOTICE_CHAT,//通知会话
    FORCED_LOGOUT//强制下线
}
