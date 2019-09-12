package com.suiyueyule.webchat.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;

/**
 * @Author: cityuu#163.com
 * @Date: 2019-08-29 15:03
 * @version: v1.0
 * @Description:
 */
@Slf4j
@Service
public class SocketHandler implements WebSocketHandler {
    /**
     * 为了保存在线用户信息，在方法中新建一个list存储一下【实际项目依据复杂度，可以存储到数据库或者缓存】
     */
    private final static Set<WebSocketSession> SESSIONS = new HashSet();

    private final static Set<String> msgIdFilter = new LinkedHashSet<>();//

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("链接成功......");

        String userName = (String) session.getAttributes().get("ws_user");

        this.sessionFilter(userName);

        SESSIONS.add(session);

        if (userName != null) {
            JSONObject obj = new JSONObject();
            // 统计一下当前登录系统的用户有多少个
            obj.put("count", SESSIONS.size()-1);
            obj.put("currentUserId",session.getId());
            obj.put("currentUserName",userName);
            users(obj,session.getId());
            session.sendMessage(new TextMessage(obj.toJSONString()));
        }
    }
    //过滤掉以前相同设备但是没有断开的缓存
    private void sessionFilter(String currentUserName) {
        for(WebSocketSession history:SESSIONS){
            String userName = (String) history.getAttributes().get("ws_user");
            if(userName.equals(currentUserName)){
                SESSIONS.remove(history);
            }
        }
    }

    private boolean msgIdFilter(String msgId){
        if(msgIdFilter.contains(msgId)){
            log.info("此消息被拒绝！");
            return false;
        }
        //给个人
        msgIdFilter.add(msgId);
        if(msgIdFilter.size()>=200){
            msgIdFilter.clear();
        }
        return true;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("处理要发送的消息");
        JSONObject msg = JSON.parseObject(message.getPayload().toString());
        JSONObject obj = new JSONObject();
        if (msg.getInteger("type") == 1) {
            //给所有人
            obj.put("msg", msg.getString("msg"));
            sendMessageToUsers(new TextMessage(obj.toJSONString()));
        } else {
            if(!this.msgIdFilter(msg.getString("msgId"))){
                log.error("重复消息，不与发送！");
                return;
            }

            String to = msg.getString("to");
            obj.put("msg", msg.getString("msg"));
            obj.put("msgId", msg.getString("msgId"));
            obj.put("fromSessionId", msg.getString("fromSessionId"));
            obj.put("fromUserName", msg.getString("fromUserName"));
            obj.put("to",to);
            obj.put("type", msg.getIntValue("type"));
            sendMessageToUser(to, new TextMessage(obj.toJSONString()));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        log.info("链接出错，关闭链接......");
        SESSIONS.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("链接关闭......" + closeStatus.toString());
        SESSIONS.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
        for (WebSocketSession user : SESSIONS) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param userName
     * @param message
     */
    public void sendMessageToUser(String userName, TextMessage message) {
        for (WebSocketSession user : SESSIONS) {
            if (user.getAttributes().get("ws_user").equals(userName)) {
                try {
                    if (user.isOpen()) {
                        user.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
               return;
            }
        }
    }

    /**
     * 将系统中的用户传送到前端
     *
     * @param obj
     */
    private void users(JSONObject obj,String sessionId) {
        Set<String> userNames = new HashSet<>();
        for (WebSocketSession webSocketSession : SESSIONS) {

            if(!webSocketSession.getId().equals(sessionId)){
                userNames.add(webSocketSession.getAttributes().get("ws_user").toString());
            }
        }
        obj.put("userList", userNames);
    }


}
