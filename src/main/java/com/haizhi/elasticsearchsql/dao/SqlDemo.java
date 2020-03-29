package com.haizhi.elasticsearchsql.dao;

import com.haizhi.elasticsearchsql.entity.Teacher;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface SqlDemo {
    String testSql();

    String paramSql(Teacher apiName);

    String paramSqlMap(Map<String,Object> apiName);
}
