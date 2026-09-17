package com.zuzuxia;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 上下文加载冒烟测试。
 *
 * 说明：该测试需要可用的 MySQL 连接。若本机数据库未启动，
 * 可用 -Dtest=ResultTest 等方式只跑单元测试，跳过本类。
 */
@SpringBootTest
class ZuzuxiaApplicationTests {

    @Test
    void contextLoads() {
    }
}
