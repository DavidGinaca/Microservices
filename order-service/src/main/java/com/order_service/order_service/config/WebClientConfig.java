package com.order_service.order_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    public WebClient webClient(){
        return WebClient.builder().build();
    }

}
