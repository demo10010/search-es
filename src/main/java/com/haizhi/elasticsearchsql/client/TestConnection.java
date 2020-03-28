package com.haizhi.elasticsearchsql.client;

import com.haizhi.elasticsearchsql.entity.DocBean;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestConnection {

    public static void getConnection() {
        //创建es客户端工具，验证环境
        ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
        //验证环境,获取es状态
        String response = clientUtil.executeHttp("_cluster/state?pretty", ClientInterface.HTTP_GET);
        List<DocBean> result = clientUtil.sql(DocBean.class, "{\"query\": \"SELECT * FROM dbclobdemo\"}");
        System.out.println(response);
    }
}
