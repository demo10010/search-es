package com.haizhi.elasticsearchsql.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.SqlElasticSearchRequestBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 支持sql语法中的 SQL Select  Delete  Where  Order By  Group By  AND & OR  Like  COUNT distinct
 * In  Between  Aliases  Not Null Date  avg()  count()  last()  max()  min()  sum()
 * Nulls  isnull()  now()  floor  split  trim  log  log10  substring  round  sqrt  concat_ws  union and minus
 */
@RestController("/sql")
@Api(description = "基于sql查询ES")
public class EsSqlController {
    @Value("${elasticSearch.hosts}")
    private String esIps;

    @GetMapping("/query/{sql}")
    @ApiOperation(value = "传入查询sql进行查询")
    public String add(@PathVariable("sql") String sql) {
        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")
                .build();
        TransportClient client = new PreBuiltTransportClient(settings);
        Arrays.asList(esIps.split(",")).forEach(ip -> {
            try {
                client.addTransportAddress(new TransportAddress(InetAddress.getByName(ip), 9300));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        });

        //执行sql查询
        try {
            //创建sql查询对象
            SearchDao searchDao = new SearchDao(client);
            //"select * from teacher where teacherId = 2"
            SqlElasticSearchRequestBuilder select = (SqlElasticSearchRequestBuilder) searchDao.explain(sql).explain();
            ActionResponse response = select.get();
            if (response instanceof SearchResponse) {
                SearchResponse searchResponse = (SearchResponse) response;
                SearchHit[] hits = searchResponse.getHits().getHits();
                List<String> list = Arrays.asList(hits).stream().map(hit -> hit.getSourceAsString()).collect(Collectors.toList());
                return list.toString();
            }
            return response.toString();
        } catch (SqlParseException e) {
            e.printStackTrace();
        } catch (SQLFeatureNotSupportedException e) {
            e.printStackTrace();
        }
        return "";
    }

}
