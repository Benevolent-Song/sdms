package com.sdms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
//开启swagger功能
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                //用于定义API主界面的信息
                .apiInfo(apiInfo())
                //用于控制接口被swagger做成文档
                .select()
                //要扫描的API(Controller)基础包
                .apis(RequestHandlerSelectors.any())
                //扫描路径选择
                .paths(PathSelectors.any())
                .build();

    }

    /**
     * 用于定义API主界面的信息
     * @return
     */
    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                //文档标题
                .title("自定义标题")
                //接口概述
                .description("自定义描述")
                //版本号
                .version("1.0")
                //定义服务的域名
                .termsOfServiceUrl(String.format("url"))
                .build();
    }

}


