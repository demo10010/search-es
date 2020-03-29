package com.haizhi.elasticsearchsql.conf;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceC3P0Adapter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.elasticsearch.xpack.sql.jdbc.jdbc.JdbcDriver;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = {"com.haizhi.elasticsearchsql.dao"}, sqlSessionFactoryRef = "esSqlSessionFactory")
public class ElasticsearchConfig {

//    static String driver = "org.elasticsearch.xpack.sql.jdbc.jdbc.JdbcDriver";
    static String elasticsearchAddress = "127.0.0.1:9200";

    @Bean(name = "esProperties")
    public static Properties connectionProperties() {
        Properties properties = new Properties();
//        properties.put("user", "test_admin");
//        properties.put("password", "x-pack-test-password");
        return properties;
    }

    @Bean(name = "esDataSource")
    public DataSource clickHouseDataSource() {
        DataSource dataSource = new DruidDataSourceC3P0Adapter();
        String address = "jdbc:es://127.0.0.1:9200";
        ((DruidDataSourceC3P0Adapter) dataSource).setDriver(new JdbcDriver());
        ((DruidDataSourceC3P0Adapter) dataSource).setJdbcUrl(address);
//        ((DruidDataSourceC3P0Adapter) dataSource).setProperties(connectionProperties());
        return dataSource;
    }

    @Bean(name = "esSqlSessionFactory")
    public SqlSessionFactory clickHouseSqlSessionFactory(@Qualifier("esDataSource") DataSource esDateSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(esDateSource);
        sessionFactory.setConfigLocation(new ClassPathResource("mybatis-conf.xml"));
        return sessionFactory.getObject();
    }
}

