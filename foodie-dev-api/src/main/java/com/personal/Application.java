package com.personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-20 14:45
 */
@MapperScan("com.personal.mapper")
@ComponentScan(basePackages = {"com.personal", "org.n3r.idworker"})
@EnableScheduling
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
