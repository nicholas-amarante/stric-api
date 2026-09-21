package com.stric.recrutamento.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AiConfig {

    @Bean
    public RestTemplate aiRestTemplate(AiProperties aiProperties, RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(15))
                .setReadTimeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
                .messageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }
}
