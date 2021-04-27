package com.lv.study.lvwechat.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * druid配置
 *
 * @author lvxiaoqiang
 * @since 2020/4/25
 */
@Configurable
public class DruidConfig {

    @Bean
    public DataSource getDataSourceFactory(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setPassword("lv");
        druidDataSource.setUrl("jdbc:oracle:thin:@47.98.135.248:1521:lv");
        druidDataSource.setUsername("lv");
        try {
            druidDataSource.setFilters("stat,wall,log4j");
            Properties properties = new Properties();
            properties.setProperty("druid.stat.mergeSql","true");
            properties.setProperty("druid.stat.slowSqlMillis","5000");
            druidDataSource.setConnectProperties(properties);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return druidDataSource;
    }

    @Bean(name = "entityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder, DataSource dataSource, JpaProperties jpaProperties) {
        Map<String,String> map = new HashMap<>();
        map.put("hibernate.dialect","org.hibernate.dialect.OracleTypesHelper");
        jpaProperties.setProperties(map);
        return builder.dataSource(dataSource).properties(jpaProperties.getProperties())
                .packages("com.myhexin.graph.domain.mysql.*") //设置实体类所在位置
                .persistenceUnit("primaryPersistenceUnit")
                .build();
    }


}
