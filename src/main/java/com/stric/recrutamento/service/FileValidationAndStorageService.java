package com.stric.recrutamento.service;

import com.stric.recrutamento.config.StorageProperties;
import com.stric.recrutamento.exception.FileValidationException;
import com.stric.recrutamento.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

/**
 * Serviço responsável pela validação em múltiplas camadas e persistência segura de arquivos PDF.
 */
@Slf4j
@Service
public class FileValidationAndStorageService {

    private final StorageProperties storageProperties;

    public FileValidationAndStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    // Magic Bytes do formato PDF: "%PDF-" correspondente em ASCII aos bytes 0x25, 0x50, 0x44, 0x46, 0x2D
    private static final byte[] PDF_MAGIC_BYTES = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D};
    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String PDF_EXTENSION = ".pdf";

    private Path rootStoragePath;

    @PostConstruct
    public void init() {
        try {
            this.rootStoragePath = Paths.get(storageProperties.getUploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(this.rootStoragePath);
            log.info("Diretório de armazenamento de currículos configurado em: {}", this.rootStoragePath);
        } catch (IOException e) {
            throw new FileValidationException("Não foi possível criar o diretório de armazenamento de uploads.", e);
        }
    }

    /**
     * Valida o arquivo em múltiplas camadas (Tamanho, Extensão, MIME Type e Magic Bytes)
     * e o persiste no disco local com nome UUID.
     *
     * @param file Arquivo MultipartFile recebido na requisição
     * @return Path absoluto do arquivo armazenado
     */
    public StoredFileResult validateAndStore(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("Nenhum arquivo de currículo foi enviado.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileValidationException("O nome do arquivo enviado é inválido.");
        }

        // 1. Validação estrita de tamanho (máximo 5MB)
        long fileSize = file.getSize();
        if (fileSize > storageProperties.getMaxFileSize()) {
            throw new FileValidationException(String.format(
                    "Tamanho do arquivo (%d bytes) excede o limite estrito permitido de %d bytes (5MB).",
                    fileSize, storageProperties.getMaxFileSize()));
        }

        // 2. Validação da extensão (.pdf case-insensitive)
        if (!originalFilename.toLowerCase().endsWith(PDF_EXTENSION)) {
            throw new FileValidationException("Extensão de arquivo inválida. Apenas arquivos com extensão .pdf são aceitos.");
        }

        // 3. Validação do MIME Type do cabeçalho
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase(PDF_MIME_TYPE)) {
            throw new FileValidationException(String.format(
                    "Tipo de conteúdo MIME inválido: '%s'. O arquivo deve ser do tipo '%s'.",
                    contentType, PDF_MIME_TYPE));
        }

        // 4. Validação de Magic Bytes (%PDF-)
        validateMagicBytes(file);

        // 5. Persistência em disco com nome aleatório baseado em UUID
        String storedFilename = UUID.randomUUID().toString() + PDF_EXTENSION;
        Path targetLocation = this.rootStoragePath.resolve(storedFilename).normalize();

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Arquivo de currículo salvo com sucesso: Nome Original='{}', Gravado='{}', Tamanho={} bytes",
                    originalFilename, storedFilename, fileSize);

            return new StoredFileResult(targetLocation.toString(), originalFilename, storedFilename, fileSize);
        } catch (IOException e) {
            log.error("Falha ao gravar arquivo no disco local: {}", targetLocation, e);
            throw new FileValidationException("Falha ao gravar o arquivo de currículo no disco local.", e);
        }
    }

    /**
     * Inspeciona os primeiros 5 bytes do fluxo de entrada para verificar a assinatura %PDF-.
     */
    public void validateMagicBytes(MultipartFile file) {
        try (InputStream is = new BufferedInputStream(file.getInputStream())) {
            byte[] header = new byte[PDF_MAGIC_BYTES.length];
            int bytesRead = is.read(header, 0, PDF_MAGIC_BYTES.length);

            if (bytesRead < PDF_MAGIC_BYTES.length) {
                throw new FileValidationException("O arquivo enviado é muito pequeno ou está corrompido para ser um PDF válido.");
            }

            if (!Arrays.equals(header, PDF_MAGIC_BYTES)) {
                String headerStr = new String(header);
                log.warn("Assinatura Magic Bytes inválida: esperada='%PDF-', encontrada='{}'", headerStr);
                throw new FileValidationException("Assinatura binária do arquivo (Magic Bytes) inválida. O arquivo não é um documento PDF genuíno.");
            }
        } catch (IOException e) {
            throw new FileValidationException("Erro ao ler os bytes do cabeçalho do arquivo para validação de Magic Bytes.", e);
        }
    }

    /**
     * Carrega o arquivo armazenado como Resource para download seguro.
     */
    public Resource loadAsResource(String storedPathString) {
        try {
            Path filePath = Paths.get(storedPathString).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Arquivo de currículo não encontrado ou inacessível no disco.");
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Caminho de arquivo inválido: " + storedPathString);
        }
    }

    /**
     * Exclui o arquivo do disco local se necessário.
     */
    public void deleteFile(String storedPathString) {
        try {
            Path filePath = Paths.get(storedPathString).normalize();
            Files.deleteIfExists(filePath);
            log.info("Arquivo deletado com sucesso: {}", storedPathString);
        } catch (IOException e) {
            log.warn("Não foi possível excluir o arquivo: {}", storedPathString, e);
        }
    }

    public record StoredFileResult(
            String fullPath,
            String originalFilename,
            String storedFilename,
            long sizeBytes
    ) {}
}
