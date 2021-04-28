package com.lv.study.lvwechat.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
        entityManagerFactoryRef="entityManagerFactoryBean",
        transactionManagerRef="transactionManagerBean",
        basePackages={"com.lv.study.lvwechat.repository"}
)
@Slf4j
public class DruidConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

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


    /**
     * 配置druid链接池
     *
     * @return 连接池
     */
    @Bean("dataSource1")
    public DataSource dataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setPassword("H91ZF494GVaPowZZxa09zbPIrBObviSA2GJWeMzwgVcQazopPtNn7ScO1H9XKrupfLHMdn5diBqvkbdqxnNGvg==");
        druidDataSource.setUrl("jdbc:oracle:thin:@47.98.135.248:1521:lv");
        druidDataSource.setUsername("lv");
        druidDataSource.setInitialSize(5); // 初始值
        druidDataSource.setMinIdle(1); // 最小空闲数
        druidDataSource.setMaxActive(20); // 最大激活数
        druidDataSource.setMaxWait(60000); // 最大等待时间
        druidDataSource.setTestWhileIdle(true); // 测试是否空闲
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000); // 多久检测是否空闲
        druidDataSource.setMinEvictableIdleTimeMillis(300000); // 连接最小生存时间
        druidDataSource.setTestOnBorrow(false); // 在获取前检测是否有效
        druidDataSource.setTestOnReturn(false); // 在归还前检测是否有效
        druidDataSource.setValidationQuery("select 'x' from dual"); // 验证sql是否有效
        druidDataSource.setPoolPreparedStatements(true); // 游标是否可利用
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20); // 最大游标范围
        try {
            // druid过滤器
            druidDataSource.setFilters("config,stat,wall,slf4j");
            // 配置过滤器中的各种属性具体设可以看
            Properties properties = new Properties();
            properties.setProperty("druid.stat.mergeSql","true");  // 合并多条相同sql
            properties.setProperty("druid.stat.logSlowSql","true"); // 打印慢sql
            properties.setProperty("druid.stat.slowSqlMillis","5000"); // 慢sql的标准
            properties.setProperty("druid.sql.Statement","warn,stdout"); // 打印方式
            properties.setProperty("config.decrypt","true"); //开启解密
            properties.setProperty("druid.log.stmt.executableSql","true"); // 打开执行sql语句日志
            properties.setProperty("config.decrypt.key","MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANAWeH/6uyx8lgsZgKBhdtX97fmXzzjUM3/0Q6Zkj1oFS32TiJa8gkqlF0nyhkqJ5xj6AqjZa9tOf39RWIOkUCMCAwEAAQ==");
            druidDataSource.setConnectProperties(properties);
        } catch (Exception e) {
            log.error("连接池创建失败",e);
        }
        return druidDataSource;
    }

    @Bean(name = "entityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(@Qualifier("dataSource1") DataSource dataSource) {
        Map<String, Object> stringObjectMap = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(jpaProperties.isShowSql());
        hibernateJpaVendorAdapter.setGenerateDdl(jpaProperties.isGenerateDdl());
        hibernateJpaVendorAdapter.setDatabase(jpaProperties.getDatabase());
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        entityManagerFactoryBean.setJpaPropertyMap(stringObjectMap);
        entityManagerFactoryBean.setPackagesToScan("com.lv.study.lvwechat.model");
        entityManagerFactoryBean.setPersistenceUnitName("db1PersistenceUnit");
        return entityManagerFactoryBean;
    }

    @Bean(name="transactionManagerBean")
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean){
        return new JpaTransactionManager(entityManagerFactoryBean.getObject());
    }

    @Bean(name="entityManager")
    public EntityManager entityManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean){
        return entityManagerFactoryBean.getObject().createEntityManager();
    }

}
