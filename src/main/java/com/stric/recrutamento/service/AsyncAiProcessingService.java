package com.stric.recrutamento.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stric.recrutamento.domain.entities.AnaliseIA;
import com.stric.recrutamento.domain.entities.Candidatura;
import com.stric.recrutamento.domain.enums.StatusCandidatura;
import com.stric.recrutamento.domain.repositories.AnaliseIARepository;
import com.stric.recrutamento.domain.repositories.CandidaturaRepository;
import com.stric.recrutamento.dto.ia.AnaliseIAResultDTO;
import com.stric.recrutamento.dto.ia.DadosExtraidosDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Serviço de processamento assíncrono do pipeline de Inteligência Artificial (RN03 e RN04).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncAiProcessingService {

    private final CandidaturaRepository candidaturaRepository;
    private final AnaliseIARepository analiseIARepository;
    private final PdfExtractorService pdfExtractorService;
    private final AiScreeningService aiScreeningService;
    private final ObjectMapper objectMapper;

    /**
     * Executa o pipeline de IA em segundo plano de forma não bloqueante.
     *
     * @param candidaturaId Identificador único da candidatura
     */
    @Async("aiTaskExecutor")
    @Transactional
    public void processarCurriculoAssincrono(UUID candidaturaId) {
        log.info("[Async-AI] Iniciando pipeline assíncrono para Candidatura ID: {}", candidaturaId);

        Candidatura candidatura = candidaturaRepository.findByIdWithDetails(candidaturaId).orElse(null);
        if (candidatura == null) {
            log.error("[Async-AI] Candidatura não encontrada para processamento: {}", candidaturaId);
            return;
        }

        try {
            // 1. Extração de texto do arquivo PDF local
            log.info("[Async-AI] Extraindo texto do PDF: {}", candidatura.getCaminhoCurriculoPdf());
            String curriculoTexto = pdfExtractorService.extractText(candidatura.getCaminhoCurriculoPdf());

            // 2. Chamada à IA (Google Gemini) com proteção anti-injection
            log.info("[Async-AI] Enviando prompt para avaliação de aderência frente à vaga '{}'", candidatura.getVaga().getTitulo());
            AnaliseIAResultDTO resultadoIA = aiScreeningService.screenResume(candidatura.getVaga(), curriculoTexto);

            // 3. Persistência da entidade AnaliseIA
            DadosExtraidosDTO dados = resultadoIA.getDadosExtraidos() != null ?
                    resultadoIA.getDadosExtraidos() : DadosExtraidosDTO.builder().build();

            String rawJson;
            try {
                rawJson = objectMapper.writeValueAsString(resultadoIA);
            } catch (Exception e) {
                rawJson = "{}";
            }

            AnaliseIA analiseIA = AnaliseIA.builder()
                    .candidatura(candidatura)
                    .scoreAderencia(resultadoIA.getScoreAderencia() != null ? resultadoIA.getScoreAderencia() : 0)
                    .resumoExecutivo(resultadoIA.getResumoExecutivo())
                    .requisitosAtendidos(resultadoIA.getRequisitosAtendidos())
                    .pontosAtencao(resultadoIA.getPontosAtencao())
                    .tempoExperienciaAnos(dados.getTempoExperienciaAnos())
                    .nivelFormacao(dados.getNivelFormacao())
                    .telefoneIdentificado(dados.getTelefoneIdentificado())
                    .emailIdentificado(dados.getEmailIdentificado())
                    .rawResponseJson(rawJson)
                    .promptUtilizado(aiScreeningService.buildAntiInjectionPrompt(candidatura.getVaga(), curriculoTexto))
                    .dataProcessamento(LocalDateTime.now())
                    .build();

            analiseIARepository.save(analiseIA);
            candidatura.setAnaliseIA(analiseIA);
            candidatura.setStatus(StatusCandidatura.CONCLUIDO);
            candidaturaRepository.save(candidatura);

            log.info("[Async-AI] Pipeline concluído com sucesso para Candidatura ID: {}. Score: {}%",
                    candidaturaId, analiseIA.getScoreAderencia());

        } catch (Exception ex) {
            log.error("[Async-AI] Falha crítica durante o processamento da IA para Candidatura ID: {}", candidaturaId, ex);

            AnaliseIA analiseErro = AnaliseIA.builder()
                    .candidatura(candidatura)
                    .scoreAderencia(0)
                    .resumoExecutivo("Ocorreu um erro durante a triagem automática deste currículo.")
                    .mensagemErro(ex.getMessage())
                    .dataProcessamento(LocalDateTime.now())
                    .build();

            analiseIARepository.save(analiseErro);
            candidatura.setAnaliseIA(analiseErro);
            candidatura.setStatus(StatusCandidatura.ERRO_PROCESSAMENTO);
            candidaturaRepository.save(candidatura);
        }
    }
}
