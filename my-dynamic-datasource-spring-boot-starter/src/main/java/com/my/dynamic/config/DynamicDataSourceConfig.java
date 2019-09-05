package com.my.dynamic.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.my.dynamic.properties.DynamicDataSourceEntity;
import com.my.dynamic.properties.DynamicDataSourceProperties;
import com.my.dynamic.utils.DynamicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@ConditionalOnProperty(
    prefix ="iot-dynamic-datasource",
    name="isopen",
    havingValue="true"
)
public class DynamicDataSourceConfig {
    private static final String DEFAULT_DATASOURCE_NAME = "DEFAULT";

    @Autowired
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    /**
     * 创建数据源
     */
    @Bean
    public Map<String, DataSource> dataSourceMap() throws Exception {
        List<DynamicDataSourceEntity> entities = dynamicDataSourceProperties.getDb();
        if(entities.size() == 0){
            throw new Exception("请正确配置数据源");
        }
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        for (DynamicDataSourceEntity entity : entities){
            Properties props = new Properties();
            props.put("driverClassName", entity.getDriverClassName());
            props.put("url", entity.getUrl());
            props.put("username", entity.getUsername());
            props.put("password", entity.getPassword());
            if(1 == entity.getIsDefault()) {
            //if("true".equals(entity.getIsDefaultDatasource())){//默认的多添加一次
                dataSourceMap.put(DEFAULT_DATASOURCE_NAME, DruidDataSourceFactory.createDataSource(props));
            }
            dataSourceMap.put(entity.getDbname(), DruidDataSourceFactory.createDataSource(props));
        }
        return dataSourceMap;
    }
    /**
     * 创建数据源池
     */
    @Bean
    @Primary
    public DynamicDataSource dataSource(Map<String, DataSource> dataSourceMap) throws Exception {
        if(null == dataSourceMap.get(DEFAULT_DATASOURCE_NAME)){
            throw new Exception("请配置默认的数据源");
        }
        DataSource defaultDataSource = dataSourceMap.get(DEFAULT_DATASOURCE_NAME);//取出默认数据源
        dataSourceMap.remove(DEFAULT_DATASOURCE_NAME);//删除默认数据源，因为在dataSourceMap存在两个该默认数据源，另一个key为dbname
        return new DynamicDataSource(defaultDataSource, dataSourceMap);
    }

    /**
     * 根据数据源创建SqlSessionFactory
     */
    //@Bean
    public SqlSessionFactory sqlSessionFactory(DynamicDataSource ds) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(ds);// 指定数据源(这个必须有，否则报错)
        // 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
        fb.setTypeAliasesPackage("com.iot.dynamic.entity");// 指定基包
        fb.setMapperLocations(resolver.getResources("classpath:mapper/Mapper*.xml"));
        return fb.getObject();
    }



    /**
     * 配置事务管理器
     */
    //@Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dataSource) throws Exception {
        return new DataSourceTransactionManager(dataSource);
    }
}
