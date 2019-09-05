package com.my.datasource.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
//@MapperScan("com.iot.metadata.*.mapper")
public class MybatisPlusConfig {
   /**
    * 分页插件，自动识别数据库类型
    * 多租户，请参考官网【插件扩展】
    */
   @Bean
   public PaginationInterceptor paginationInterceptor() {
      return new PaginationInterceptor();
   }
   /**
    * SQL执行效率插件
    */
   @Bean
   @ConditionalOnProperty(name = "showsql", havingValue = "true")
   public PerformanceInterceptor performanceInterceptor() {
      return new PerformanceInterceptor();
   }
}