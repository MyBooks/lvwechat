package com.lv.study.lvwechat.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
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
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef="entityManagerFactory",
        transactionManagerRef="transactionManager",
        basePackages={"com.lv.study.lvwechat.model","com.lv.study.lvwechat.repository"}
)
public class DruidConfig {

    @Bean
    public ServletRegistrationBean druidServlet() {
        // 现在要进行druid监控的配置处理操作
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        // 控制台管理用户名
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        // 控制台管理密码
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        // 是否可以重置数据源，禁用HTML页面上的“Reset All”功能
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean ;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean() ;
        filterRegistrationBean.setFilter(new WebStatFilter());
        //所有请求进行监控处理
        filterRegistrationBean.addUrlPatterns("/*");
        //添加不需要忽略的格式信息
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.css,/druid/*");
        return filterRegistrationBean ;
    }


    @Bean
    public DataSource dataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setPassword("1997@LVlv");
        druidDataSource.setUrl("jdbc:oracle:thin:@47.98.135.248:1521:lv");
        druidDataSource.setUsername("lv");
        try {
            //druidDataSource.setFilters("stat,wall,log4j");
            Properties properties = new Properties();
            properties.setProperty("druid.stat.mergeSql","true");
            properties.setProperty("druid.stat.slowSqlMillis","5000");
            druidDataSource.setConnectProperties(properties);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return druidDataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder, DataSource dataSource, JpaProperties jpaProperties) {
        Map<String,String> map = new HashMap<>();
        map.put("dialect","org.hibernate.dialect.OracleTypesHelper");
        jpaProperties.setProperties(map);
        return builder.dataSource(dataSource).properties(jpaProperties.getProperties())
                .packages("com.lv.study.lvwechat.model") //设置实体类所在位置
                .build();
    }

    @Bean(name="transactionManager")
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean){
        return new JpaTransactionManager(entityManagerFactoryBean.getObject());
    }

    @Bean(name="entityManager")
    public EntityManager entityManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean){
        return entityManagerFactoryBean.getObject().createEntityManager();
    }

}
