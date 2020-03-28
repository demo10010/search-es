package com.haizhi.elasticsearchsql.conf;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Assert;

@Configuration
@ComponentScan(basePackageClasses = ESClientSpringFactory.class)
public class EsClientConf {
    @Value("${elasticSearch.hosts}")
    private String hosts;

    @Value("${elasticSearch.port}")
    private int port;

    @Value("${elasticSearch.client.connectNum}")
    private Integer connectNum;

    @Value("${elasticSearch.client.connectPerRoute}")
    private Integer connectPerRoute;

    @Bean
    public HttpHost[] httpHost() {
        Assert.hasLength(this.hosts, "无效的es连接");
        String[] hostStr = hosts.split(",");
        int hostCount = hostStr.length;
        HttpHost[] httpHosts = new HttpHost[hostCount];
        for (int i = 0; i < hostCount; i++) {
            httpHosts[i] = new HttpHost(hostStr[i], port, "http");
        }
        return httpHosts;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public ESClientSpringFactory getFactory() {
        return ESClientSpringFactory.build(httpHost(), connectNum, connectPerRoute);
    }

    @Bean
    @Scope("singleton")
    public RestClient getRestClient() {
        return getFactory().getClient();
    }

    @Bean
    @Scope("singleton")
    public RestHighLevelClient getRHLClient() {
        return getFactory().getRhlClient();
    }

}