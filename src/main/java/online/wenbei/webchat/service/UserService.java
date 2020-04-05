package online.wenbei.webchat.service;

import online.wenbei.webchat.constance.CacheKeys;
import online.wenbei.webchat.util.MD5Util;
import online.wenbei.webchat.util.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author: cityuu#163.com
 * @Date: 2019-08-29 11:27
 * @version: v1.0
 * @Description:
 */

@Slf4j
@Service("userService")
public class UserService extends BaseRedisService {

    //@Resource
    //private RocketMQTemplate rocketMQTemplate;
    /**
     * 通过识别码登录的用户
     * @param code
     * @param type 0 发起端 1被聊端
     * @return
     * @throws Exception
     */
    public ResultData login(String code,Integer type, String deviceId) throws Exception {

        Object value = this.hget(CacheKeys.getChatMapKey(code),type.toString());

        //如果用户的MD5值不相等，则踢掉之前会话的人
        if(!StringUtils.isEmpty(value)){
            if(!MD5Util.getMD5(value.toString()).equals(MD5Util.getMD5(deviceId))){
                //先给该用户发一条强制下线的消息 然后让新客户端连接
                //rocketMQTemplate.convertAndSend(Chat.textBuilder(ChatType.FORCED_LOGOUT.ordinal(),1,"您已经被强制下线！"));
                this.hset(CacheKeys.getChatMapKey(code),type.toString(),deviceId);
            }
            return ResultData.error("您已经登录过了,无需重复登录！");
        }else{
            this.hset(CacheKeys.getChatMapKey(code),type.toString(),deviceId);
            return ResultData.success();
        }

    }
}
