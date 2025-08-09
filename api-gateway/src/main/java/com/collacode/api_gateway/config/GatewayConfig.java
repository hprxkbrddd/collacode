package com.collacode.api_gateway.config;

import com.collacode.api_gateway.component.AuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public AuthFilter authFilter() {
        return new AuthFilter();
    }
}
