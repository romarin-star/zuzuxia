package com.zuzuxia;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 租租侠租赁平台 —— 后端服务启动类
 */
@SpringBootApplication
@MapperScan("com.zuzuxia.mapper")
public class ZuzuxiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuzuxiaApplication.class, args);
    }
}
