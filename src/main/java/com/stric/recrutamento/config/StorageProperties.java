package com.stric.recrutamento.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    /**
     * Diretório local para gravação segura dos currículos em PDF.
     */
    private String uploadDir = "./storage/uploads/curriculos";

    /**
     * Tamanho máximo permitido em bytes (padrão 5MB = 5 * 1024 * 1024).
     */
    private long maxFileSize = 5 * 1024 * 1024;

    public StorageProperties() {
    }
}
