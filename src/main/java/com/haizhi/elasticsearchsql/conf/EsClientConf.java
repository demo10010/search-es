package com.haizhi.elasticsearchsql.conf;

import com.haizhi.elasticsearchsql.factory.RhlClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@ComponentScan(basePackageClasses = RhlClientFactory.class)
@Slf4j
public class EsClientConf {
    @Value("${elasticSearch.hosts}")
    private String hosts;

    @Value("${elasticSearch.port}")
    private int port;

    @Value("${elasticSearch.client.connectNum}")
    private Integer connectNum;

    @Value("${elasticSearch.client.connectPerRoute}")
    private Integer connectPerRoute;

    @Value("${elasticSearch.clusterName}")
    private String clusterName;


    private static final int tcpPort = 9300;

    @Bean(initMethod = "init", destroyMethod = "close")
    public RhlClientFactory getFactory() {
        return RhlClientFactory.getRhlClientFactory().build(httpHost(), connectNum, connectPerRoute);
    }

    @Bean
    public RestClient getRestClient() {
        System.err.println(getFactory().getClient().toString());
        return getFactory().getClient();
    }

    public RestHighLevelClient getRhlClient() {
        System.err.println(getFactory().getRhlClient().toString());
        return getFactory().getRhlClient();
    }

    @Bean
    public TransportClient getTcClient() {
        Assert.hasLength(hosts, "无效的es连接");
        Settings settings = Settings.builder().put("cluster.name", clusterName).build();
        TransportClient transportClient = new PreBuiltTransportClient(settings);
        String[] ips = hosts.split(",");
        try {
            for (int i = 0; i < ips.length; i++) {
                transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName(ips[i]), tcpPort));
            }
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
        return transportClient;
    }

    private HttpHost[] httpHost() {
        Assert.hasLength(this.hosts, "无效的es连接");
        String[] hostStr = hosts.split(",");
        int hostCount = hostStr.length;
        HttpHost[] httpHosts = new HttpHost[hostCount];
        for (int i = 0; i < hostCount; i++) {
            httpHosts[i] = new HttpHost(hostStr[i], port, "http");
        }
        return httpHosts;
    }

}