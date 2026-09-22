package com.stric.recrutamento.service;

import com.stric.recrutamento.config.OcrProperties;
import com.stric.recrutamento.exception.FileValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para extração segura de texto de arquivos PDF utilizando Apache PDFBox.
 * Implementa fallback OCR via Tesseract (Tess4J) para documentos escaneados (imagens).
 *
 * Fluxo:
 * 1. Tenta extrair texto diretamente via PDFTextStripper (PDFs com camada de texto)
 * 2. Se nenhum texto relevante for encontrado, renderiza cada página como imagem
 *    e aplica OCR com Tesseract para reconhecimento óptico de caracteres
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdfExtractorService {

    private final OcrProperties ocrProperties;

    /** Número mínimo de caracteres para considerar que o PDF tem texto legível */
    private static final int MIN_TEXT_THRESHOLD = 30;

    /** DPI para renderização de páginas PDF como imagem para OCR */
    private static final float OCR_RENDER_DPI = 300f;

    /**
     * Extrai o conteúdo textual completo de um arquivo PDF armazenado localmente.
     * Se o PDF não possuir camada de texto (scan/imagem), utiliza OCR via Tesseract.
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

            // 1ª tentativa: extração direta de texto (PDFs com texto embutido)
            String text = extractDirectText(document);

            if (text != null && text.trim().length() >= MIN_TEXT_THRESHOLD) {
                String sanitized = sanitizeText(text);
                log.info("Texto extraído diretamente do PDF via PDFTextStripper: {} caracteres", sanitized.length());
                return sanitized;
            }

            // 2ª tentativa: OCR via Tesseract para PDFs escaneados (imagens)
            log.info("PDF sem camada de texto legível. Iniciando OCR via Tesseract para: {}", filePath);
            String ocrText = extractTextViaOcr(document);

            if (ocrText != null && ocrText.trim().length() >= MIN_TEXT_THRESHOLD) {
                String sanitized = sanitizeText(ocrText);
                log.info("Texto extraído via OCR (Tesseract) do PDF: {} caracteres", sanitized.length());
                return sanitized;
            }

            log.warn("Nenhum texto pôde ser extraído do PDF mesmo com OCR: {}", filePath);
            return "[Documento PDF sem texto legível. Nem a extração direta nem o OCR conseguiram recuperar conteúdo textual.]";

        } catch (IOException e) {
            log.error("Erro ao extrair texto do PDF via PDFBox: {}", filePath, e);
            throw new FileValidationException("Falha ao ler a estrutura do arquivo PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Extração direta de texto usando PDFTextStripper (para PDFs com camada de texto).
     */
    private String extractDirectText(PDDocument document) {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        } catch (IOException e) {
            log.warn("Falha na extração direta de texto via PDFTextStripper: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Renderiza cada página do PDF como imagem e aplica OCR com Tesseract.
     * Utiliza resolução de 300 DPI para boa qualidade de reconhecimento.
     */
    private String extractTextViaOcr(PDDocument document) {
        try {
            Tesseract tesseract = new Tesseract();

            // Configuração do diretório tessdata com os arquivos de treinamento
            File tessdataDir = new File(ocrProperties.getTessdataPath());
            if (!tessdataDir.exists() || !tessdataDir.isDirectory()) {
                log.error("Diretório tessdata não encontrado em: {}. OCR não disponível.", tessdataDir.getAbsolutePath());
                log.error("Faça o download dos arquivos de treinamento em: https://github.com/tesseract-ocr/tessdata");
                return null;
            }

            tesseract.setDatapath(tessdataDir.getAbsolutePath());
            tesseract.setLanguage(ocrProperties.getLanguage());

            // Configurações de qualidade do Tesseract
            tesseract.setPageSegMode(1);  // Automatic page segmentation with OSD
            tesseract.setOcrEngineMode(1); // LSTM neural net mode

            PDFRenderer renderer = new PDFRenderer(document);
            List<String> pageTexts = new ArrayList<>();

            int totalPages = document.getNumberOfPages();
            log.info("Processando {} página(s) do PDF via OCR (DPI: {})...", totalPages, OCR_RENDER_DPI);

            for (int page = 0; page < totalPages; page++) {
                try {
                    // Renderiza a página como imagem em alta resolução
                    BufferedImage image = renderer.renderImageWithDPI(page, OCR_RENDER_DPI, ImageType.RGB);

                    // Aplica OCR na imagem renderizada
                    String pageText = tesseract.doOCR(image);

                    if (pageText != null && !pageText.trim().isEmpty()) {
                        pageTexts.add(pageText.trim());
                        log.debug("OCR página {}/{}: {} caracteres extraídos", page + 1, totalPages, pageText.trim().length());
                    }
                } catch (TesseractException e) {
                    log.warn("Falha no OCR da página {}/{}: {}", page + 1, totalPages, e.getMessage());
                }
            }

            if (pageTexts.isEmpty()) {
                return null;
            }

            return String.join("\n\n", pageTexts);

        } catch (Exception e) {
            log.error("Erro durante o processamento OCR via Tesseract: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Sanitização básica de caracteres de controle mantendo pontuação e quebras de linha.
     */
    private String sanitizeText(String text) {
        if (text == null) return "";
        return text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
    }
}
