package com.sdms;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
//@MapperScan("com.sdms.mapper")//扫描mapper接口所在的包
public class SdmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SdmsApplication.class, args);
    }
}
