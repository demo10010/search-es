package com.haizhi.elasticsearchsql.controller;

import com.haizhi.elasticsearchsql.dao.SqlDemo;
import com.haizhi.elasticsearchsql.entity.Teacher;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.SqlElasticSearchRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLFeatureNotSupportedException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 支持sql语法中的 SQL Select  Delete  Where  Order By  Group By  AND & OR  Like  COUNT distinct
 * In  Between  Aliases  Not Null Date  avg()  count()  last()  max()  min()  sum()
 * Nulls  isnull()  now()  floor  split  trim  log  log10  substring  round  sqrt  concat_ws  union and minus
 */
@RestController("/esSql")
@Api(description = "基于sql查询ES")
public class EsSqlController {
//    @Value("${elasticSearch.hosts}")
//    private String esIps;

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private SqlDemo sqlDemo;

    @GetMapping("/query/{sql}")
    @ApiOperation(value = "传入查询sql进行查询")
    public String add(@PathVariable("sql") String sql) {

        //执行sql查询
        try {
            //创建sql查询对象
            SearchDao searchDao = new SearchDao(transportClient);
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

    @GetMapping("/query2/{sql}")
    @ApiOperation(value = "传入查询sql进行查询")
    public String test() {
        Teacher myname = Teacher.builder().name("myname").build();
        System.out.println(sqlDemo.paramSql(myname));
        //这里面值填了一个参数，这个参数的意思是：com.demo.db.mapper.DemoMapper是xml文件里的namespace路径。
        //insert是你的id，即<insert id="insert">
        Configuration configuration = sqlSessionFactory.getConfiguration();
        Collection<MappedStatement> mappedStatements = configuration.getMappedStatements();
        mappedStatements.forEach(m -> {
            System.out.println(m.getId());
            System.out.println(m.toString());
            ParameterMap parameterMap = m.getParameterMap();
            String boundSql = m.getBoundSql(parameterMap).getSql();
            System.out.println(boundSql);
        });


        Map<String, Object> map = new HashMap<>();
        map.put("userName", "admin");
        map.put("userPassword", "admin");

        String name = SqlDemo.class.getName();
        String paramSql = getSqlBy(name, "paramSql", map);
        String paramSqlMap = getSqlBy(name, "paramSqlMap", map);
        System.out.println(paramSqlMap);

        return paramSql;
    }

    private String getSqlBy(String clazz, String methodName, Object parameterObject) {
        String sqlId = clazz + "." + methodName;
//        DefalutSqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Configuration configuration1 = sqlSession.getConfiguration();
        MappedStatement statements = configuration1.getMappedStatement(sqlId);
//        BaseExecutor

//        MappedStatement statements = sqlSessionFactory.getConfiguration().getMappedStatement(sqlId);
        ParameterMap parameterMap = statements.getParameterMap();
        List<ParameterMapping> parameterMappings = parameterMap.getParameterMappings();
        parameterMappings.forEach(s -> {
            System.out.println(s.getProperty());
            System.out.println(s.getExpression());
        });
//        BoundSql boundSql = statements.getBoundSql(parameterObject);

//        String sqlId = statements.getId();
        BoundSql boundSql = statements.getBoundSql(parameterObject);
        Configuration configuration = statements.getConfiguration();
        String formatSql = formatSql(sqlId, configuration, boundSql);
        System.out.println(formatSql);

//        boundSql.setAdditionalParameter("name",parameterObject.get("name"));
        String sql = boundSql.getSql();
        return formatSql;
    }


    private String formatSql(String sqls, Configuration configuration, BoundSql boundSql) {

        //美化sql
//        sql = beautifySql(sql);

        //填充占位符, 目前基本不用mybatis存储过程调用,故此处不做考虑
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        List<String> parameters = new ArrayList<>();
        if (parameterMappings != null) {
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    //  参数值
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    //  获取参数名称
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 获取参数值
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        // 如果是单个值则直接赋值
                        value = parameterObject;
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }

                    if (value instanceof Number) {
                        parameters.add(String.valueOf(value));
                    } else {
                        StringBuilder builder = new StringBuilder();
                        builder.append("'");
                        if (value instanceof Date) {
                            builder.append(dateTimeFormatter.get().format((Date) value));
                        } else if (value instanceof String) {
                            builder.append(value);
                        }
                        builder.append("'");
                        parameters.add(builder.toString());
                    }
                }
            }
        }


        for (String value : parameters) {
            sqls = sqls.replaceFirst("\\?", value);
        }
        return sqls;
    }

    private static ThreadLocal<SimpleDateFormat> dateTimeFormatter = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static String beautifySql(String sql) {
        sql = sql.replaceAll("[\\s\n ]+", " ");
        return sql;
    }
//
//    private static String getSql(Class<SqlDemo> clazz, String methodName, Map<String, Object> parameterMap) throws Exception {
//        String sqlId = clazz.getName() + "." + methodName;
//        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
//        sqlSessionFactory.setDataSource(new DruidDataSource());
//
////        JdbcDriver
//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        //配置mapper文件位置
//        sqlSessionFactory.setMapperLocations(resolver.getResources("D:\\code\\search-es\\src\\main\\resources\\es\\sqlMappers\\"));
//
//        SqlSessionFactory object = sqlSessionFactory.getObject();
//        Collection<MappedStatement> statements = object.getConfiguration().getMappedStatements();
//        MappedStatement ms = object.getConfiguration().getMappedStatement(sqlId);
//        BoundSql boundSql = ms.getBoundSql(parameterMap);
//        return boundSql.getSql();
//    }

}
