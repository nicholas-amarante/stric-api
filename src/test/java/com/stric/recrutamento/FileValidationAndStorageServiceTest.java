package com.stric.recrutamento;

import com.stric.recrutamento.config.StorageProperties;
import com.stric.recrutamento.exception.FileValidationException;
import com.stric.recrutamento.service.FileValidationAndStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileValidationAndStorageServiceTest {

    private FileValidationAndStorageService storageService;
    private StorageProperties storageProperties;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        storageProperties = new StorageProperties();
        storageProperties.setUploadDir(tempDir.toString());
        storageProperties.setMaxFileSize(5 * 1024 * 1024);

        storageService = new FileValidationAndStorageService(storageProperties);
        storageService.init();
    }

    @Test
    @DisplayName("Deve aceitar e armazenar arquivo PDF genuíno com Magic Bytes %PDF-")
    void deveAceitarPdfValido() {
        byte[] pdfContent = "%PDF-1.7\nExemplo de conteudo de curriculo".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile(
                "arquivo",
                "curriculo_joao.pdf",
                "application/pdf",
                pdfContent
        );

        FileValidationAndStorageService.StoredFileResult result = storageService.validateAndStore(file);

        assertNotNull(result);
        assertNotNull(result.fullPath());
        assertEquals("curriculo_joao.pdf", result.originalFilename());
        assertTrue(result.storedFilename().endsWith(".pdf"));
        assertEquals(pdfContent.length, result.sizeBytes());
    }

    @Test
    @DisplayName("Deve rejeitar arquivo com extensão inválida")
    void deveRejeitarExtensaoInvalida() {
        byte[] pdfContent = "%PDF-1.7\nTeste".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile(
                "arquivo",
                "curriculo.docx",
                "application/pdf",
                pdfContent
        );

        assertThrows(FileValidationException.class, () -> storageService.validateAndStore(file));
    }

    @Test
    @DisplayName("Deve rejeitar arquivo com MIME Type inválido")
    void deveRejeitarMimeTypeInvalido() {
        byte[] pdfContent = "%PDF-1.7\nTeste".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile(
                "arquivo",
                "curriculo.pdf",
                "text/plain",
                pdfContent
        );

        assertThrows(FileValidationException.class, () -> storageService.validateAndStore(file));
    }

    @Test
    @DisplayName("Deve rejeitar arquivo disfarçado sem Magic Bytes %PDF-")
    void deveRejeitarMagicBytesInvalidos() {
        byte[] fakeContent = "ESTE E UM ARQUIVO DE TEXTO DISFARCADO".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile(
                "arquivo",
                "curriculo_falso.pdf",
                "application/pdf",
                fakeContent
        );

        FileValidationException ex = assertThrows(FileValidationException.class, () -> storageService.validateAndStore(file));
        assertTrue(ex.getMessage().contains("Magic Bytes"));
    }

    @Test
    @DisplayName("Deve rejeitar arquivo que excede limite estrito de 5MB")
    void deveRejeitarArquivoMaiorQue5MB() {
        byte[] oversized = new byte[(5 * 1024 * 1024) + 10];
        System.arraycopy("%PDF-".getBytes(StandardCharsets.UTF_8), 0, oversized, 0, 5);

        MockMultipartFile file = new MockMultipartFile(
                "arquivo",
                "curriculo_pesado.pdf",
                "application/pdf",
                oversized
        );

        FileValidationException ex = assertThrows(FileValidationException.class, () -> storageService.validateAndStore(file));
        assertTrue(ex.getMessage().contains("5MB") || ex.getMessage().contains("excede"));
    }
}
