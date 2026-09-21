package com.stric.recrutamento.controller;

import com.stric.recrutamento.dto.candidatura.CandidaturaDetailDTO;
import com.stric.recrutamento.dto.candidatura.CandidaturaMaskedDTO;
import com.stric.recrutamento.dto.candidatura.SelecaoEntrevistaDTO;
import com.stric.recrutamento.dto.candidatura.UploadResponseDTO;
import com.stric.recrutamento.dto.common.ApiResponse;
import com.stric.recrutamento.service.CandidaturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/candidaturas")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "3. Candidaturas & Triagem IA", description = "Endpoints de submissão de currículo, triagem assíncrona por IA, mascaramento LGPD e seleção")
public class CandidaturaController {

    private final CandidaturaService candidaturaService;

    @PostMapping(value = "/vagas/{vagaId}/submeter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(
            summary = "Submeter currículo em PDF (RN02 e RN03)",
            description = "Recebe o arquivo PDF do currículo, valida Magic Bytes (%PDF-), tamanho (<= 5MB) e unicidade de submissão. " +
                          "Retorna HTTP 202 com status EM_ANALISE enquanto o pipeline de IA processa em segundo plano via @Async."
    )
    public ResponseEntity<ApiResponse<UploadResponseDTO>> submeterCandidatura(
            @PathVariable UUID vagaId,
            @Parameter(description = "Arquivo do currículo em formato PDF genuíno (máx 5MB)")
            @RequestParam("arquivo") MultipartFile arquivo) {

        UploadResponseDTO response = candidaturaService.submeterCandidatura(vagaId, arquivo);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.ok("Candidatura submetida com sucesso. Análise de IA em andamento.", response));
    }

    @GetMapping("/vagas/{vagaId}")
    @PreAuthorize("hasRole('RECRUITER')")
    @Operation(
            summary = "Listar candidatos de uma vaga com dados mascarados (LGPD)",
            description = "Retorna a lista de candidatos ordenados por score de IA, com telefone e e-mail mascarados até a seleção de entrevista."
    )
    public ResponseEntity<ApiResponse<List<CandidaturaMaskedDTO>>> listarCandidatosPorVaga(@PathVariable UUID vagaId) {
        List<CandidaturaMaskedDTO> candidatos = candidaturaService.listarCandidatosPorVaga(vagaId);
        return ResponseEntity.ok(ApiResponse.ok(candidatos));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obter detalhes da candidatura",
            description = "Retorna os dados da candidatura e o resultado da triagem de IA para o candidato proprietário ou recrutador responsável."
    )
    public ResponseEntity<ApiResponse<CandidaturaDetailDTO>> obterDetalhes(@PathVariable UUID id) {
        CandidaturaDetailDTO detalhes = candidaturaService.obterDetalhes(id);
        return ResponseEntity.ok(ApiResponse.ok(detalhes));
    }

    @PatchMapping("/{id}/selecionar-entrevista")
    @PreAuthorize("hasRole('RECRUITER')")
    @Operation(
            summary = "Selecionar candidato para entrevista e liberar dados de contato (LGPD)",
            description = "Altera o status da candidatura para SELECIONADO_ENTREVISTA, libera os dados de contato completos e grava o log de auditoria com o recrutador."
    )
    public ResponseEntity<ApiResponse<CandidaturaDetailDTO>> selecionarParaEntrevista(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) SelecaoEntrevistaDTO dto) {

        CandidaturaDetailDTO response = candidaturaService.selecionarParaEntrevista(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Candidato selecionado para entrevista. Dados de contato liberados e auditoria registrada.", response));
    }

    @GetMapping("/{id}/download-pdf")
    @Operation(
            summary = "Baixar arquivo original do currículo PDF",
            description = "Permite o download do arquivo PDF do currículo armazenado de forma segura no servidor."
    )
    public ResponseEntity<Resource> baixarCurriculoPdf(@PathVariable UUID id) {
        Resource resource = candidaturaService.baixarCurriculoPdf(id);
        String nomeOriginal = candidaturaService.obterNomeArquivoOriginal(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeOriginal + "\"")
                .body(resource);
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(
            summary = "Listar candidaturas do candidato logado",
            description = "Retorna o histórico e status de todas as candidaturas submetidas pelo candidato autenticado."
    )
    public ResponseEntity<ApiResponse<List<CandidaturaDetailDTO>>> listarMinhasCandidaturas() {
        List<CandidaturaDetailDTO> candidaturas = candidaturaService.listarMinhasCandidaturas();
        return ResponseEntity.ok(ApiResponse.ok(candidaturas));
    }
}
