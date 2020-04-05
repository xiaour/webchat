package online.wenbei.webchat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: cityuu#163.com
 * @Date: 2019-09-09 12:52
 * @version: v1.0
 * @Description:
 */
@Controller
@RequestMapping("/")
public class PageController {


    @RequestMapping("/")
    public String login(){
        return "index";
    }

}
