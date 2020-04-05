package online.wenbei.webchat.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: cityuu#163.com
 * @Date: 2019-08-29 11:59
 * @version: v1.0
 * @Description: 消息结构
 */
@Data
@ToString
@Accessors(chain = true)
public class Chat {

    private Integer chatType;//会话类型

    private String text;//消息内容

    private Integer messageType;//消息类型 1.文本、2.图片 3.文件

    private Date sendTime;//消息发送时间

    /**
     * 文本类消息构建
     * @param chatType
     * @param messageType
     * @param text
     * @return
     */
    public static Chat textBuilder(Integer chatType,Integer messageType,String text){
        return new Chat().setChatType(chatType)
                .setMessageType(messageType)
                .setSendTime(new Date())
                .setText(text);
    }
}
