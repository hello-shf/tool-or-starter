package com.my.async.config;

import cn.hutool.json.JSONUtil;
import com.my.async.properties.AsyncEntity;
import com.my.async.properties.AsyncProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync
@Configuration
@ConditionalOnProperty//存在对应配置信息时初始化该配置类
        (
                prefix = "iot-async",//存在配置前缀hello
                name = "isopen",
                havingValue = "true",//开启
                matchIfMissing = true//缺失检查
        )
@EnableConfigurationProperties(AsyncProperties.class)//开启使用映射实体对象
public class AsyncConfig {

    @Autowired
    private AsyncProperties asyncProperties;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 动态生成多个线程池
     * 这个方法返回Runnable只是一个幌子，最重要的是执行方法里面的代码
     * @return
     * @throws Exception
     */
    @Bean
    public Runnable dynamicConfiguration() throws Exception {

        ConfigurableApplicationContext context = (ConfigurableApplicationContext)applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)context.getBeanFactory();

        List<AsyncEntity> asyncEntityList = asyncProperties.getList();
        for (AsyncEntity asyncEntity : asyncEntityList) {
            //开始注册异步线程池
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(ThreadPoolTaskExecutor.class);
            beanDefinitionBuilder.addPropertyValue("corePoolSize", asyncEntity.getCorePoolSize());
            beanDefinitionBuilder.addPropertyValue("maxPoolSize", asyncEntity.getMaxPoolSize());
            beanDefinitionBuilder.addPropertyValue("queueCapacity", asyncEntity.getQueueCapacity());
            beanDefinitionBuilder.addPropertyValue("keepAliveSeconds", asyncEntity.getKeepAliveSeconds());
            beanDefinitionBuilder.addPropertyValue("threadNamePrefix", asyncEntity.getThreadName()+"-");
            //线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
            beanDefinitionBuilder.addPropertyValue("waitForTasksToCompleteOnShutdown", true);
            //该方法用来设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
            beanDefinitionBuilder.addPropertyValue("awaitTerminationSeconds", 60);
            /**
             * 程池对拒绝任务的处理策略：这里采用了CallerRunsPolicy策略，当线程池没有处理能力的时候，该策略会直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
             */
            beanDefinitionBuilder.addPropertyValue("rejectedExecutionHandler",new ThreadPoolExecutor.CallerRunsPolicy());
            /**
             * 注册到spring容器中
             */
            beanFactory.registerBeanDefinition(asyncEntity.getThreadName(), beanDefinitionBuilder.getBeanDefinition());
        }
        return null;
    }

    /**
     * 核心线程数10：线程池创建时候初始化的线程数
     *最大线程数20：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
     *缓冲队列200：用来缓冲执行任务的队列
     *允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
     *线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
     *线程池对拒绝任务的处理策略：这里采用了CallerRunsPolicy策略，当线程池没有处理能力的时候，该策略会直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
     * @return
     */
//    @Bean(AsyncEnum.DEFAULT_EXECUTOR)
//    public Executor defaultExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);
//        executor.setMaxPoolSize(20);
//        executor.setQueueCapacity(20000);
//        executor.setKeepAliveSeconds(60);
//        executor.setThreadNamePrefix(AsyncEnum.DEFAULT_EXECUTOR+"-");
          //该方法就是这里的关键，用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean，这样这些异步任务的销毁就会先于Redis线程池的销毁
//        executor.setWaitForTasksToCompleteOnShutdown(true);
         //该方法用来设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
//        executor.setAwaitTerminationSeconds(60);
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        return executor;
//    }
}
