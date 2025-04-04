package com.sdms;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.sdms.util")//
@ComponentScan(basePackages = "com.sdms.shiro")
//@MapperScan("com.sdms.mapper")//扫描mapper接口所在的包
public class SdmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SdmsApplication.class, args);
    }
}
