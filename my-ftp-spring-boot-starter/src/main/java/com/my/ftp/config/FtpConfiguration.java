package com.my.ftp.config;

import com.my.ftp.properties.FtpOptionProperties;
import com.my.ftp.service.IotFtpService;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;


@Configuration//开启配置
@EnableConfigurationProperties(FtpOptionProperties.class)//开启使用映射实体对象
@ConditionalOnClass({IotFtpService.class,GenericObjectPool.class, FTPClient.class})//存在IotFtpService时初始化该配置类
@ConditionalOnProperty//存在对应配置信息时初始化该配置类
        (
                prefix = "iot-ftp",//存在配置前缀hello
                name = "isopen",
                havingValue = "true",//开启
                matchIfMissing = true//缺失检查
        )
public class FtpConfiguration {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FtpOptionProperties ftpOptionProperties;

    private ObjectPool<FTPClient> pool;

    /**
     * 预先加载FTPClient连接到对象池中
     * @param initialSize 初始化连接数
     * @param maxIdle 最大空闲连接数
     */
    private void preLoadingFtpClient(Integer initialSize, int maxIdle) {
        if (initialSize == null || initialSize <= 0) {
            return;
        }

        int size = Math.min(initialSize.intValue(), maxIdle);
        for (int i = 0; i < size; i++) {
            try {
                pool.addObject();
            } catch (Exception e) {
                log.error("preLoadingFtpClient error...", e);
            }
        }
    }
    @PreDestroy
    public void destroy() {
        if (pool != null) {
            pool.close();
            log.info("销毁ftpClientPool...");
        }
    }
    /**
     * 根据条件判断不存在ElsService时初始化新bean到SpringIoc
     * GenericObjectPool参数解析:https://www.jianshu.com/p/5cb54a5bfc3a
     * @return
     */
    @Bean//创建HelloService实体bean
    @ConditionalOnMissingBean(IotFtpService.class)//缺失HelloService实体bean时，初始化HelloService并添加到SpringIoc
    public IotFtpService iotFtpService()
            throws Exception{
        log.info(">>>The IotFtpService Not Found，Execute Creat New Bean.");
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        //当连接池资源耗尽时，等待时间，超出则抛异常，默认为-1即永不超时
        poolConfig.setMaxWaitMillis(10000);
        //当这个值为true的时候，maxWaitMillis参数才能生效。为false的时候，当连接池没资源，则立马抛异常。默认为true
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        poolConfig.setSoftMinEvictableIdleTimeMillis(50000);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        pool = new GenericObjectPool<>(new FtpClientPooledObjectFactory(ftpOptionProperties), poolConfig);
        preLoadingFtpClient(ftpOptionProperties.getInitialSize(), poolConfig.getMaxIdle());
        IotFtpService iotFtpService =new IotFtpService();
        iotFtpService.setFtpClientPool(pool);
        iotFtpService.setHasInit(true);
        return iotFtpService;
    }


    /**
     * FtpClient对象工厂类
     */
    static class FtpClientPooledObjectFactory implements PooledObjectFactory<FTPClient> {
        private Logger log = LoggerFactory.getLogger(this.getClass());

        private FtpOptionProperties props;

        public FtpClientPooledObjectFactory(FtpOptionProperties props) {
            this.props = props;
        }

        @Override
        public PooledObject<FTPClient> makeObject() throws Exception {
            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect(props.getHost(), props.getPort());
                ftpClient.login(props.getUsername(), props.getPassword());
                log.info("连接FTP服务器返回码{}", ftpClient.getReplyCode());
                ftpClient.setBufferSize(props.getBufferSize());
                ftpClient.setControlEncoding(props.getEncoding());
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                return new DefaultPooledObject<>(ftpClient);
            } catch (Exception e) {
                log.error("建立FTP连接失败", e);
                if (ftpClient.isAvailable()) {
                    ftpClient.disconnect();
                }
                ftpClient = null;
                throw new Exception("建立FTP连接失败", e);
            }
        }

        @Override
        public void destroyObject(PooledObject<FTPClient> p) throws Exception {
            FTPClient ftpClient = getObject(p);
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        }

        @Override
        public boolean validateObject(PooledObject<FTPClient> p) {
            FTPClient ftpClient = getObject(p);
            if (ftpClient == null || !ftpClient.isConnected()) {
                return false;
            }
            try {
                ftpClient.changeWorkingDirectory("/");
                return true;
            } catch (Exception e) {
                log.error("验证FTP连接失败::{}",e.getMessage());
                return false;
            }
        }

        @Override
        public void activateObject(PooledObject<FTPClient> p) throws Exception {
        }

        @Override
        public void passivateObject(PooledObject<FTPClient> p) throws Exception {
        }

        private FTPClient getObject(PooledObject<FTPClient> p) {
            if (p == null || p.getObject() == null) {
                return null;
            }
            return p.getObject();
        }

    }
}