package com.zb.myswagger2.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhangbin on 2018/12/19.
 */
@RestController
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String index(){
        return "This is a index !";
    }

    @RequestMapping(value = "/home", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String home(){
        return "Hello Swagger2!";
    }

    @RequestMapping(value = "/sayhi", method = RequestMethod.POST)
    @ResponseBody
    public String sayHi(String name){
        return "Hello " + name;
    }
}
