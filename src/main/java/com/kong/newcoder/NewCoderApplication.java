package com.kong.newcoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NewCoderApplication {

    public static void main(String[] args) {
        //启动tomcat 自动创建spring容器 装载bean
        SpringApplication.run(NewCoderApplication.class, args);
    }

}
