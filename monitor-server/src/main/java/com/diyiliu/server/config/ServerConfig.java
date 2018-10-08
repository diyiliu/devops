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

    @Value("${server.port}")
    private Integer serverPort;

    @Bean
    public MonitorServer monitorServer() {
        MonitorServer server = new MonitorServer();
        server.setPort(serverPort);
        server.init();

        return server;
    }

}
