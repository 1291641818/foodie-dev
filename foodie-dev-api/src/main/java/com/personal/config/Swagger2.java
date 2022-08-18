package com.personal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-23 14:00
 *
 */

@Configuration
@EnableSwagger2
public class Swagger2 {

    //      http://localhost:8088/swagger-ui.html  原地址
    //      http://localhost:8088/doc.html  换肤地址

    //配置swagger2核心配置docket
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)         //指定api类型为swagger2
                .apiInfo(apiInfo())                             //用于定义api文档汇总信息
                .select().apis(RequestHandlerSelectors
                        .basePackage("com.personal.controller"))//指定controller包
                .paths(PathSelectors.any())                     //所有controller
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("mall接口api")                                     //文档标题
                .contact(new Contact("personal",
                        "www.personal.com", "aXuan@qq.com"))//联系人信息
                .description("接口文档")                                  //详细信息
                .version("1.0.1")                                         //文档版本号
                .termsOfServiceUrl("www.personal.com")                    //网站地址
                .build();
    }

}








