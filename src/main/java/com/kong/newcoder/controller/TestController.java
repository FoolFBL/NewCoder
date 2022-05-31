package com.kong.newcoder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shijiu
 */
@RestController
public class TestController {

    @RequestMapping("/h1")
    public String hello(){

        return "hello sbhr";
    }
}
