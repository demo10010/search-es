package com.haizhi.elasticsearchsql.util;

import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理es rest client组件实例
 */
@Component
public class ESClient {
    //基础不需要xml
    private static ClientInterface restClient;

    @Value("${es.basePath}")
    private static String basePath;

    @Value("${es.indexs}")
    private static String indexs;
    //客户端
    private static Map<String, ClientInterface> client = new ConcurrentHashMap<>();

    /**
     * 获取操作默认的es集群的客户端工具组件
     *
     * @return
     */
    public static ClientInterface restClient() {
        if (restClient == null) {
            synchronized (ClientInterface.class) {
                if (restClient == null) {
                    restClient = ElasticSearchHelper.getRestClientUtil();
                }
            }
        }
        return restClient;
    }

    /**
     * 获取操作配置文件的es集群的客户端工具组件
     *
     * @return
     */
    public static ClientInterface restClient(String index) {
        if (client.isEmpty()) {
            String[] indexArr = indexs.split(",");
            for (int i = 0; i < indexArr.length; i++) {
                client.put(indexArr[i], ElasticSearchHelper.getConfigRestClientUtil(basePath + indexArr[i] + ".xml"));
            }
        }
        return client.get(index);
    }

}
