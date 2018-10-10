package com.diyiliu.server.config;

import com.diyiliu.server.netty.MonitorServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description: ServerConfig
 * Author: DIYILIU
 * Update: 2018-10-08 17:23
 */

@Configuration
public class ServerConfig {

    @Value("${port}")
    private Integer port;

    @Bean
    public MonitorServer monitorServer() {
        MonitorServer server = new MonitorServer();
        server.setPort(port);
        server.init();

        return server;
    }

}
