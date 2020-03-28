package com.haizhi.elasticsearchsql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class ElasticsearchSqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchSqlApplication.class, args);
//        getConnection();
    }

//
//    public static void getConnection() {
//        //创建es客户端工具，验证环境
//        ClientInterfaceNew clientUtil = ElasticSearchHelper.getRestClientUtil();
//        //验证环境,获取es状态
//        String response = ((ClientInterface) clientUtil).executeHttp("_cluster/state?pretty", ClientInterface.HTTP_GET);
//        System.out.println(response);
//    }

}
