package com.personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-08-20 22:54
 */
@MapperScan("com.personal.mapper")
@ComponentScan(basePackages = {"com.personal", "org.n3r.idworker"})
@SpringBootApplication
public class SsoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SsoApplication.class, args);
    }
}
