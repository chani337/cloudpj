package com.cloudclass.mood.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ModelServiceConfig {

    // docker-compose 상의 서비스 이름(mood-model)을 그대로 호스트로 사용합니다.
    @Value("${mood.model.base-url:http://mood-model:8000}")
    private String modelBaseUrl;

    @Bean
    public RestClient modelRestClient() {
        return RestClient.builder()
                .baseUrl(modelBaseUrl)
                .build();
    }
}
