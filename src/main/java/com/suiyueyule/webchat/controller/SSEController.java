package com.suiyueyule.webchat.controller;

import com.suiyueyule.webchat.service.UserService;
import com.suiyueyule.webchat.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: zhangtao@suiyueyule.com
 * @Date: 2019-08-28 18:15
 * @version: v1.0
 * @Description:
 */

@Controller
@RequestMapping("/sse")
public class SSEController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param request
     * @return
     */
    @GetMapping("/login/{type}/{code}")
    public ResultData login(@PathVariable Integer type,@PathVariable String code, @RequestParam String deviceId, HttpServletRequest request) throws Exception {
        return userService.login(code,type,deviceId);
    }


    /**
     * 断开
     * @param request
     * @return
     */
    @GetMapping("/logout/{code}")
    public String logout(@PathVariable String code,HttpServletRequest request) {
        return request.getHeader("user-agent");
    }



    /**
     * 第一个用户
     *
     * @param request
     * @return
     */
    @RequestMapping("/chat1")
    public String chat1(HttpServletRequest request) {
        // 假设用户tom登录,存储到session中
        request.getSession().setAttribute("WEBSOCKET_USERNAME", "tom");
        return "chat1";
    }

    /**
     * 第二个用户登录
     *
     * @param request
     * @return
     */
    @RequestMapping("/chat2")
    public String chat2(HttpServletRequest request) {
        // 假设用户jerry登录,存储到session中
        request.getSession().setAttribute("WEBSOCKET_USERNAME", "jerry");
        return "chat2";
    }

    /**
     * 第三个用户登录
     *
     * @param request
     * @return
     */
    @RequestMapping("/chat3")
    public String chat3(HttpServletRequest request) {
        // 假设用户jack登录,存储到session中
        request.getSession().setAttribute("WEBSOCKET_USERNAME", "jack");
        return "chat3";
    }



}
