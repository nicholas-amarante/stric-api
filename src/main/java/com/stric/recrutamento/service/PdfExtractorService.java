package com.stric.recrutamento.service;

import com.stric.recrutamento.exception.FileValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Serviço para extração segura de texto de arquivos PDF utilizando Apache PDFBox.
 */
@Slf4j
@Service
public class PdfExtractorService {

    /**
     * Extrai o conteúdo textual completo de um arquivo PDF armazenado localmente.
     *
     * @param filePath Caminho completo do arquivo PDF no sistema de arquivos
     * @return Texto extraído e sanitizado
     */
    public String extractText(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            throw new FileValidationException("Arquivo PDF não encontrado ou sem permissão de leitura: " + filePath);
        }

        try (PDDocument document = Loader.loadPDF(file)) {
            if (document.isEncrypted()) {
                throw new FileValidationException("O arquivo PDF está protegido por senha ou criptografado.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                log.warn("Nenhum texto pôde ser extraído do PDF (possível documento escaneado/imagem): {}", filePath);
                return "[Documento PDF sem camada de texto legível por OCR/PDFBox]";
            }

            // Sanitização básica de caracteres de controle mantendo pontuação e quebras de linha
            String sanitized = text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
            log.info("Texto extraído com sucesso do PDF: {} caracteres", sanitized.length());
            return sanitized;
        } catch (IOException e) {
            log.error("Erro ao extrair texto do PDF via PDFBox: {}", filePath, e);
            throw new FileValidationException("Falha ao ler a estrutura do arquivo PDF: " + e.getMessage(), e);
        }
    }
}
