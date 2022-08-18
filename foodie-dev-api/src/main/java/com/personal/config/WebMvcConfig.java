package com.personal.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-29 16:29
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 将RestTemplate交给spring管理
     * @param builder
     * @return
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }

    /**
     * 实现静态资源的映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                //本地资源映射 windows
                //.addResourceLocations("file:G:\\code\\cache file\\img\\")
                //映射swagger2
                .addResourceLocations("classpath:/META-INF/resources/")
                //本地资源映射 unix

                .addResourceLocations("file:/usr/local/temp/images/");
    }
}
