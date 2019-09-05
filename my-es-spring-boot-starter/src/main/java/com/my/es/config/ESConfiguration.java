package com.my.es.config;

import com.my.es.properties.ESProperties;
import com.my.es.service.ESSearchService;
import com.my.es.service.IotEsTemplate;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.util.logging.Logger;


@Configuration//开启配置
@EnableConfigurationProperties(ESProperties.class)//开启使用映射实体对象
@ConditionalOnClass(ESSearchService.class)//存在ESSearchService时初始化该配置类
@ConditionalOnProperty//存在对应配置信息时初始化该配置类
        (
                prefix = "iot-es",//存在配置前缀hello
                name = "isopen",
                havingValue = "true",//开启
                matchIfMissing = true//缺失检查
        )
public class ESConfiguration{
    @Autowired
    private ESProperties esProperties;
    /**
     * 根据条件判断不存在ElsService时初始化新bean到SpringIoc
     * @return
     */
    @Bean//创建HelloService实体bean
    @ConditionalOnMissingBean(ESSearchService.class)//缺失HelloService实体bean时，初始化HelloService并添加到SpringIoc
    public ESSearchService eSSearchService()
            throws Exception{
        ESSearchService eSSearchService = new ESSearchService();
        // 配置信息
        Settings.Builder builder = Settings.builder().put("cluster.name", esProperties.getClusterName())
                .put("client.transport.sniff", true)// 增加嗅探机制，找到ES集群
                .put("thread_pool.search.size", Integer.parseInt(esProperties.getPoolSize()));// 增加线程池个数，暂时设为5
        Settings esSetting;
        TransportClient client;
        if(esProperties.getXPack()!=null){
            esSetting=builder.put("xpack.security.transport.ssl.enabled",esProperties.getXPack().getSslEnabled())
                    .put("client.transport.sniff",true)
                    .put("xpack.security.transport.ssl.verification_mode",esProperties.getXPack().getVerificationMode())
                    .put("xpack.security.transport.ssl.keystore.path",esProperties.getXPack().getKeystorePath())
                    .put("xpack.security.transport.ssl.truststore.path",esProperties.getXPack().getTruststorePath())
                    .put("xpack.security.user",""+esProperties.getXPack().getUser()+":"+esProperties.getXPack().getPassword()+"").build();
            client = new PreBuiltXPackTransportClient(esSetting);
        }else{
            client = new PreBuiltTransportClient(builder.build());
        }
        String clusterNodes = esProperties.getClusterNodes();
        if (!"".equals(clusterNodes)){
            for (String nodes:clusterNodes.split(",")) {
                String InetSocket [] = nodes.split(":");
                String  Address = InetSocket[0];
                Integer  port = Integer.valueOf(InetSocket[1]);
                client.addTransportAddress(new
                        InetSocketTransportAddress(InetAddress.getByName(Address),port));
            }
        }
        eSSearchService.setClient(client);
        return eSSearchService;
    }


    /**
     * 根据条件判断不存在ElsService时初始化新bean到SpringIoc
     * @return
     */
    @Bean//创建HelloService实体bean
    @ConditionalOnMissingBean(IotEsTemplate.class)//缺失HelloService实体bean时，初始化HelloService并添加到SpringIoc
    public IotEsTemplate iotEsTemplate()
            throws Exception{
        IotEsTemplate iotEsTemplate = new IotEsTemplate();
        // 配置信息
        Settings.Builder builder = Settings.builder().put("cluster.name", esProperties.getClusterName())
                .put("client.transport.sniff", true)// 增加嗅探机制，找到ES集群
                .put("thread_pool.search.size", Integer.parseInt(esProperties.getPoolSize()));// 增加线程池个数，暂时设为5
        Settings esSetting;
        TransportClient client;
        if(esProperties.getXPack()!=null){
            esSetting=builder.put("xpack.security.transport.ssl.enabled",esProperties.getXPack().getSslEnabled())
                    .put("client.transport.sniff",true)
                    .put("xpack.security.transport.ssl.verification_mode",esProperties.getXPack().getVerificationMode())
                    .put("xpack.security.transport.ssl.keystore.path",esProperties.getXPack().getKeystorePath())
                    .put("xpack.security.transport.ssl.truststore.path",esProperties.getXPack().getTruststorePath())
                    .put("xpack.security.user",""+esProperties.getXPack().getUser()+":"+esProperties.getXPack().getPassword()+"").build();
            client = new PreBuiltXPackTransportClient(esSetting);
        }else{
            client = new PreBuiltTransportClient(builder.build());
        }
        String clusterNodes = esProperties.getClusterNodes();
        if (!"".equals(clusterNodes)){
            for (String nodes:clusterNodes.split(",")) {
                String InetSocket [] = nodes.split(":");
                String  Address = InetSocket[0];
                Integer  port = Integer.valueOf(InetSocket[1]);
                client.addTransportAddress(new
                        InetSocketTransportAddress(InetAddress.getByName(Address),port));
            }
        }
        iotEsTemplate.setClient(client);
        return iotEsTemplate;
    }

}