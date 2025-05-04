package com.hgl.hglaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 请别把我整破防
 */
@SpringBootApplication
@MapperScan("com.hgl.hglaiagent.mapper")
public class HglAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(HglAiAgentApplication.class, args);
    }

}
