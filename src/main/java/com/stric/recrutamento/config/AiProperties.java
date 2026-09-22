package com.stric.recrutamento.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.gemini")
public class AiProperties {

    /**
     * Chave de API do Google Gemini.
     * Carregada exclusivamente via variável de ambiente GEMINI_API_KEY.
     */
    private String apiKey = "";

    /**
     * Modelo utilizado (ex: gemini-1.5-flash, gemini-1.5-pro).
     */
    private String model = "gemini-1.5-flash";

    /**
     * Endpoint base da API do Gemini.
     */
    private String endpoint = "https://generativelanguage.googleapis.com/v1beta/models";

    /**
     * Timeout em segundos para chamadas à IA.
     */
    private int timeoutSeconds = 45;

    // REMOVIDO: mockFallbackEnabled — O sistema agora exige obrigatoriamente a API do Gemini.
}
