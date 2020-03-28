package com.haizhi.elasticsearchsql.service.impl;

import com.haizhi.elasticsearchsql.service.EsCurlDemoService;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EsCurlDemoServiceImpl implements EsCurlDemoService {

    @Qualifier("getRHLClient")
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void add() {
        IndexRequest indexRequest = new IndexRequest("my_test", "tweet", "1");//文档ID
        indexRequest.source("{\"teacherId\":\"mydoc\",\"name\":\"namemydoc\",\"age\":null,\"sex\":\"男\"}", XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete() {
        DeleteRequest deleteRequest = new DeleteRequest("my_test", "tweet", "1");
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update() {
        UpdateRequest updateRequest = new UpdateRequest("my_test", "tweet", "1");
        updateRequest.doc(query());
        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String query() {
        SearchRequest searchRequest = new SearchRequest("my_test");
        searchRequest.types("tweet");
        QueryBuilders.termQuery("name","namemydoc");
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            if (hits != null) return hits[0].toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
