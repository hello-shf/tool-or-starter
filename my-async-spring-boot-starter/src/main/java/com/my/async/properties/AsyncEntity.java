package com.my.async.properties;

import lombok.Data;


@Data
public class AsyncEntity {
    /**
     * 核心线程数：线程池创建时候初始化的线程数
     */
    private int corePoolSize=10;
    /**
     * 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
     */
    private int maxPoolSize=20;
    /**
     * 缓冲队列：用来缓冲执行任务的队列
     */
    private int queueCapacity=2000;
    /**
     * 允许线程的空闲时间(秒)：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
     */
    private int keepAliveSeconds=60;
    /**
     *设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
     */
    private int awaitTerminationSeconds=10;
    /**
     * 线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
     */
    private String threadName;
}
