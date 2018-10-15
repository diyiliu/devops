package com.diyiliu.client.config;

import com.diyiliu.client.netty.MonitorClient;
import com.diyiliu.plugin.util.SpringUtil;
import com.diyiliu.util.OsMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * Description: ClientConfig
 * Author: DIYILIU
 * Update: 2018-10-09 09:04
 */

@Configuration
@PropertySource("classpath:config.properties")
public class ClientConfig {

    @Resource
    private Environment environment;

    @Bean
    public MonitorClient monitorClient(){
        MonitorClient client = new MonitorClient();
        client.setHost(environment.getProperty("host"));
        client.setPort(environment.getProperty("port", Integer.class));
        client.init();

        return client;
    }

    @Bean
    public OsMonitor monitor(){
        String host = environment.getProperty("localhost");
        return new OsMonitor(host);
    }

    @Bean
    public SpringUtil springUtil(){

        return new SpringUtil();
    }
}
