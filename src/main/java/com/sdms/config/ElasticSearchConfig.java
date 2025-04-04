package com.sdms.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration//内部也有@Component注解,所以也可以实现类的注入
public class ElasticSearchConfig {

    //将第三方库的类或方法(不是用户定义的)交给spring管理,可以用@Autowired创建实例
    //@Configuration+@Bean共同才能实现
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("es",9200,"http")
                        //在本地部署使用localhost
                )
        );
        return client;
    }
}
