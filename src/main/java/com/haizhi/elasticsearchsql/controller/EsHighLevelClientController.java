package com.haizhi.elasticsearchsql.controller;

import com.haizhi.elasticsearchsql.service.EsCurlDemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.SqlElasticSearchRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
@RestController()
@RequestMapping("/hlc")
@Api(description = "基于sql查询ES")
@Slf4j
public class EsHighLevelClientController {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private EsCurlDemoService esCurlDemoService;

    @GetMapping("/query2/{indexName}")
    @ApiOperation(value = "传入查询sql进行查询")
    public String add(@PathVariable("indexName") String indexName) {
        return createIndex(restHighLevelClient,indexName);
    }

    @GetMapping("/add")
    @ApiOperation(value = "传入查询sql进行查询")
    public void add(){
        esCurlDemoService.add();
    }

    @GetMapping("/delete")
    @ApiOperation(value = "delete")
    public void delete(){
        esCurlDemoService.delete();
    }

    @GetMapping("/update")
    @ApiOperation(value = "update")
    public void update(){
        esCurlDemoService.update();
    }

    @GetMapping("/query")
    @ApiOperation(value = "query")
    public void query(){
        esCurlDemoService.query();
    }

    private String createIndex(RestHighLevelClient client, String indexName) {
        CreateIndexRequest request = new CreateIndexRequest(indexName);//创建索引
        //创建的每个索引都可以有与之关联的特定设置。
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );
        //创建索引时创建文档类型映射
        request.mapping("tweet",//类型定义
                "  {\n" +
                        "    \"tweet\": {\n" +
                        "      \"properties\": {\n" +
                        "        \"message\": {\n" +
                        "          \"type\": \"text\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }",//类型映射，需要的是一个JSON字符串
                XContentType.JSON);

        //为索引设置一个别名
        request.alias(
                new Alias("twitter_alias")
        );
        //可选参数
        request.timeout(TimeValue.timeValueMinutes(2));//超时,等待所有节点被确认(使用TimeValue方式)
        //request.timeout("2m");//超时,等待所有节点被确认(使用字符串方式)

        request.masterNodeTimeout(TimeValue.timeValueMinutes(1));//连接master节点的超时时间(使用TimeValue方式)
        //request.masterNodeTimeout("1m");//连接master节点的超时时间(使用字符串方式)

        request.waitForActiveShards(2);//在创建索引API返回响应之前等待的活动分片副本的数量，以int形式表示。
        //request.waitForActiveShards(ActiveShardCount.DEFAULT);//在创建索引API返回响应之前等待的活动分片副本的数量，以ActiveShardCount形式表示。

        //同步执行
        CreateIndexResponse createIndexResponse = null;
        try {
            createIndexResponse = client.indices().create(request,RequestOptions.DEFAULT);
            log.info(createIndexResponse.isShardsAcknowledged()+"");
            log.info(createIndexResponse.isAcknowledged()+"");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //异步执行
        //异步执行创建索引请求需要将CreateIndexRequest实例和ActionListener实例传递给异步方法：
        //CreateIndexResponse的典型监听器如下所示：
        //异步方法不会阻塞并立即返回。
//        ActionListener<CreateIndexResponse> listener = new ActionListener<CreateIndexResponse>() {
//            @Override
//            public void onResponse(CreateIndexResponse createIndexResponse) {
//                //如果执行成功，则调用onResponse方法;
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                //如果失败，则调用onFailure方法。
//            }
//        };
//        client.indices().create(request, listener);//要执行的CreateIndexRequest和执行完成时要使用的ActionListener

        return "";
    }

}
