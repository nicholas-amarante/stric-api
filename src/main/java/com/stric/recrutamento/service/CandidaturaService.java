package com.stric.recrutamento.service;

import com.stric.recrutamento.domain.entities.AuditoriaAcessoContato;
import com.stric.recrutamento.domain.entities.Candidatura;
import com.stric.recrutamento.domain.entities.Usuario;
import com.stric.recrutamento.domain.entities.Vaga;
import com.stric.recrutamento.domain.enums.Role;
import com.stric.recrutamento.domain.enums.StatusCandidatura;
import com.stric.recrutamento.domain.enums.StatusVaga;
import com.stric.recrutamento.domain.repositories.AuditoriaAcessoContatoRepository;
import com.stric.recrutamento.domain.repositories.CandidaturaRepository;
import com.stric.recrutamento.domain.repositories.VagaRepository;
import com.stric.recrutamento.dto.candidatura.CandidaturaDetailDTO;
import com.stric.recrutamento.dto.candidatura.CandidaturaMaskedDTO;
import com.stric.recrutamento.dto.candidatura.SelecaoEntrevistaDTO;
import com.stric.recrutamento.dto.candidatura.UploadResponseDTO;
import com.stric.recrutamento.exception.BusinessException;
import com.stric.recrutamento.exception.DuplicateApplicationException;
import com.stric.recrutamento.exception.ResourceNotFoundException;
import com.stric.recrutamento.exception.UnauthorizedAccessException;
import com.stric.recrutamento.service.FileValidationAndStorageService.StoredFileResult;
import com.stric.recrutamento.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
public class CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final VagaRepository vagaRepository;
    private final AuditoriaAcessoContatoRepository auditoriaRepository;
    private final FileValidationAndStorageService fileStorageService;
    private final AsyncAiProcessingService asyncAiProcessingService;
    private final DataMaskingService dataMaskingService;
    private final SecurityUtils securityUtils;

    public CandidaturaService(CandidaturaRepository candidaturaRepository,
                              VagaRepository vagaRepository,
                              AuditoriaAcessoContatoRepository auditoriaRepository,
                              FileValidationAndStorageService fileStorageService,
                              AsyncAiProcessingService asyncAiProcessingService,
                              DataMaskingService dataMaskingService,
                              SecurityUtils securityUtils) {
        this.candidaturaRepository = candidaturaRepository;
        this.vagaRepository = vagaRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.fileStorageService = fileStorageService;
        this.asyncAiProcessingService = asyncAiProcessingService;
        this.dataMaskingService = dataMaskingService;
        this.securityUtils = securityUtils;
    }

    /**
     * Submete a candidatura com upload de PDF e inicia a triagem de IA em segundo plano (RN02, RN03, RN05).
     */
    @Transactional
    public UploadResponseDTO submeterCandidatura(UUID vagaId, MultipartFile arquivo) {
        Usuario candidato = securityUtils.getCurrentUser();

        if (candidato.getRole() != Role.ROLE_CANDIDATE) {
            throw new UnauthorizedAccessException("Apenas usuários com perfil de Candidato (ROLE_CANDIDATE) podem submeter currículos.");
        }

        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga", vagaId));

        if (vaga.getStatus() != StatusVaga.ABERTA) {
            throw new BusinessException("Não é possível submeter candidatura para uma vaga com status " + vaga.getStatus() + ".");
        }

        // Validação de Prazo Limite de Candidatura
        if (vaga.getDataLimiteInscricao() != null && LocalDate.now().isAfter(vaga.getDataLimiteInscricao())) {
            throw new BusinessException("O período de inscrições para esta vaga encerrou em " +
                    vaga.getDataLimiteInscricao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
        }

        // Permite múltiplas candidaturas: apenas registra no log se já houver candidatura prévia
        if (candidaturaRepository.existsByVagaIdAndCandidatoId(vaga.getId(), candidato.getId())) {
            log.info("Candidato {} já possui candidatura anterior para a vaga {}. Permitindo nova submissão.",
                    candidato.getEmail(), vaga.getTitulo());
        }

        // Validação multicamadas e armazenamento seguro do arquivo PDF em disco
        StoredFileResult storedResult = fileStorageService.validateAndStore(arquivo);

        Candidatura candidatura = Candidatura.builder()
                .vaga(vaga)
                .candidato(candidato)
                .caminhoCurriculoPdf(storedResult.fullPath())
                .nomeOriginalArquivo(storedResult.originalFilename())
                .tamanhoArquivoBytes(storedResult.sizeBytes())
                .status(StatusCandidatura.EM_ANALISE)
                .build();

        candidatura = candidaturaRepository.save(candidatura);
        log.info("Candidatura registrada: ID={}, Candidato={}, Vaga={}",
                candidatura.getId(), candidato.getEmail(), vaga.getTitulo());

        // RN03: Dispara o pipeline assíncrono de IA após a efetivação (commit) da transação
        final UUID candidaturaId = candidatura.getId();
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    asyncAiProcessingService.processarCurriculoAssincrono(candidaturaId);
                }
            });
        } else {
            asyncAiProcessingService.processarCurriculoAssincrono(candidaturaId);
        }

        // Resposta imediata HTTP 202 com status EM_ANALISE
        return UploadResponseDTO.builder()
                .candidaturaId(candidatura.getId())
                .vagaId(vaga.getId())
                .tituloVaga(vaga.getTitulo())
                .status(StatusCandidatura.EM_ANALISE)
                .mensagem("Currículo recebido com sucesso. A triagem inteligente por IA está sendo processada em segundo plano.")
                .dataSubmissao(LocalDateTime.now())
                .build();
    }

    /**
     * Lista candidatos de uma vaga para o recrutador com dados de contato mascarados (LGPD).
     */
    @Transactional(readOnly = true)
    public List<CandidaturaMaskedDTO> listarCandidatosPorVaga(UUID vagaId) {
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga", vagaId));

        validarPropriedadeVaga(vaga);

        List<Candidatura> candidaturas = candidaturaRepository.findByVagaIdWithDetails(vagaId);

        return candidaturas.stream()
                .map(dataMaskingService::toMaskedDTO)
                .collect(Collectors.toList());
    }

    /**
     * Altera o status da candidatura para SELECIONADO_ENTREVISTA e libera os dados de contato completos,
     * registrando log de auditoria LGPD.
     */
    @Transactional
    public CandidaturaDetailDTO selecionarParaEntrevista(UUID candidaturaId, SelecaoEntrevistaDTO dto) {
        Candidatura candidatura = candidaturaRepository.findByIdWithDetails(candidaturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidatura", candidaturaId));

        validarPropriedadeVaga(candidatura.getVaga());

        Usuario recrutador = securityUtils.getCurrentUser();

        candidatura.setStatus(StatusCandidatura.SELECIONADO_ENTREVISTA);
        candidatura = candidaturaRepository.save(candidatura);

        // Registro de auditoria LGPD
        String justificativa = dto != null && dto.getJustificativa() != null ?
                dto.getJustificativa() : "Candidato selecionado para processo de entrevista técnica/comportamental.";

        AuditoriaAcessoContato auditoria = AuditoriaAcessoContato.builder()
                .candidatura(candidatura)
                .recrutador(recrutador)
                .acao("LIBERACAO_DADOS_CONTATO_ENTREVISTA")
                .justificativa(justificativa)
                .build();

        auditoriaRepository.save(auditoria);
        candidatura.getAuditorias().add(auditoria);

        log.info("Auditoria LGPD gravada: Recrutador '{}' liberou dados de contato da Candidatura ID={}",
                recrutador.getEmail(), candidaturaId);

        return CandidaturaDetailDTO.fromEntity(candidatura);
    }

    /**
     * Obtém os detalhes completos de uma candidatura específica.
     */
    @Transactional(readOnly = true)
    public CandidaturaDetailDTO obterDetalhes(UUID candidaturaId) {
        Candidatura candidatura = candidaturaRepository.findByIdWithDetails(candidaturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidatura", candidaturaId));

        Usuario usuarioLogado = securityUtils.getCurrentUser();

        boolean isCandidatoDono = candidatura.getCandidato().getId().equals(usuarioLogado.getId());
        boolean isRecrutadorDono = candidatura.getVaga().getRecrutador().getId().equals(usuarioLogado.getId());

        if (!isCandidatoDono && !isRecrutadorDono) {
            throw new UnauthorizedAccessException("Você não tem autorização para visualizar os detalhes desta candidatura.");
        }

        return CandidaturaDetailDTO.fromEntity(candidatura);
    }

    /**
     * Download seguro do arquivo PDF do currículo.
     */
    @Transactional(readOnly = true)
    public Resource baixarCurriculoPdf(UUID candidaturaId) {
        Candidatura candidatura = candidaturaRepository.findByIdWithDetails(candidaturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidatura", candidaturaId));

        Usuario usuarioLogado = securityUtils.getCurrentUser();

        boolean isCandidatoDono = candidatura.getCandidato().getId().equals(usuarioLogado.getId());
        boolean isRecrutadorDono = candidatura.getVaga().getRecrutador().getId().equals(usuarioLogado.getId());

        if (!isCandidatoDono && !isRecrutadorDono) {
            throw new UnauthorizedAccessException("Você não tem autorização para baixar este arquivo de currículo.");
        }

        // RN-LGPD / Blind Screening: Recrutador só pode baixar o PDF completo após selecionar para entrevista
        if (isRecrutadorDono && !isCandidatoDono && candidatura.getStatus() != StatusCandidatura.SELECIONADO_ENTREVISTA) {
            throw new BusinessException("O download do currículo com dados completos está bloqueado por conformidade LGPD. Selecione o candidato para entrevista para desbloquear o arquivo.");
        }

        return fileStorageService.loadAsResource(candidatura.getCaminhoCurriculoPdf());
    }

    /**
     * Retorna o nome do arquivo original para cabeçalho de download.
     */
    @Transactional(readOnly = true)
    public String obterNomeArquivoOriginal(UUID candidaturaId) {
        Candidatura candidatura = candidaturaRepository.findById(candidaturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidatura", candidaturaId));
        return candidatura.getNomeOriginalArquivo();
    }

    /**
     * Lista todas as candidaturas do candidato autenticado.
     */
    @Transactional(readOnly = true)
    public List<CandidaturaDetailDTO> listarMinhasCandidaturas() {
        Usuario candidato = securityUtils.getCurrentUser();
        return candidaturaRepository.findByCandidatoIdWithDetails(candidato.getId()).stream()
                .map(CandidaturaDetailDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void validarPropriedadeVaga(Vaga vaga) {
        Usuario usuarioLogado = securityUtils.getCurrentUser();
        if (!vaga.getRecrutador().getId().equals(usuarioLogado.getId())) {
            throw new UnauthorizedAccessException("Você não possui permissão para gerenciar candidaturas desta vaga.");
        }
    }
}
