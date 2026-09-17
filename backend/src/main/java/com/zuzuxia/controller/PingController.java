package com.zuzuxia.controller;

import com.zuzuxia.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 连通性探测接口。
 *
 * <p>用途：前端首页调用它来确认"Vite proxy → Spring Boot"这条链路是通的。
 * 该接口不查数据库，因此即使 MySQL 未启动也能返回，便于分层定位问题。
 */
@RestController
@RequestMapping("/api")
public class PingController {

    @GetMapping("/ping")
    public Result<Map<String, Object>> ping() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("app", "zuzuxia");
        data.put("version", "1.0.0");
        data.put("time", LocalDateTime.now().toString());
        return Result.success(data);
    }
}
