package com.stric.recrutamento.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ocr")
public class OcrProperties {

    /**
     * Caminho para o diretório tessdata contendo os arquivos de treinamento do Tesseract.
     * Padrão: ./tessdata (relativo à raiz do projeto).
     */
    private String tessdataPath = "./tessdata";

    /**
     * Idiomas para reconhecimento OCR (formato Tesseract: "por+eng" para Português + Inglês).
     */
    private String language = "por+eng";
}
