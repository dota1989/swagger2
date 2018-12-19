package com.zb.myswagger2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhangbin on 2018/12/19.
 */
@Controller
public class TestController {

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    public String test(String name){
        return "Hello " + name + ", this is a test !";
    }
}
