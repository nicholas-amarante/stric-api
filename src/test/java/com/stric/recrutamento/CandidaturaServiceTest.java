package com.stric.recrutamento;

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
import com.stric.recrutamento.dto.candidatura.SelecaoEntrevistaDTO;
import com.stric.recrutamento.dto.candidatura.UploadResponseDTO;
import com.stric.recrutamento.exception.DuplicateApplicationException;
import com.stric.recrutamento.exception.BusinessException;
import org.springframework.core.io.Resource;
import com.stric.recrutamento.service.*;
import com.stric.recrutamento.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidaturaServiceTest {

    @Mock
    private CandidaturaRepository candidaturaRepository;

    @Mock
    private VagaRepository vagaRepository;

    @Mock
    private AuditoriaAcessoContatoRepository auditoriaRepository;

    @Mock
    private FileValidationAndStorageService fileStorageService;

    @Mock
    private AsyncAiProcessingService asyncAiProcessingService;

    @Mock
    private DataMaskingService dataMaskingService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private CandidaturaService candidaturaService;

    private Usuario candidato;
    private Usuario recrutador;
    private Vaga vaga;
    private UUID vagaId;

    @BeforeEach
    void setUp() {
        candidato = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Candidato Teste")
                .email("candidato@teste.com")
                .role(Role.ROLE_CANDIDATE)
                .build();

        recrutador = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Recrutador Teste")
                .email("recrutador@empresa.com")
                .role(Role.ROLE_RECRUITER)
                .build();

        vagaId = UUID.randomUUID();
        vaga = Vaga.builder()
                .id(vagaId)
                .titulo("Desenvolvedor Java Sênior")
                .status(StatusVaga.ABERTA)
                .recrutador(recrutador)
                .build();
    }

    @Test
    @DisplayName("Deve permitir submissão mesmo quando candidato já possui candidatura anterior para a vaga")
    void devePermitirSubmissaoMesmoJaExistindoCandidaturaAnterior() {
        when(securityUtils.getCurrentUser()).thenReturn(candidato);
        when(vagaRepository.findById(vagaId)).thenReturn(Optional.of(vaga));
        when(candidaturaRepository.existsByVagaIdAndCandidatoId(vaga.getId(), candidato.getId())).thenReturn(true);

        FileValidationAndStorageService.StoredFileResult storedResult = new FileValidationAndStorageService.StoredFileResult(
                "/tmp/storage/arquivo2.pdf", "curriculo2.pdf", "uuid2.pdf", 1024L
        );
        when(fileStorageService.validateAndStore(any())).thenReturn(storedResult);

        UUID candidaturaId = UUID.randomUUID();
        Candidatura candidaturaSalva = Candidatura.builder()
                .id(candidaturaId)
                .vaga(vaga)
                .candidato(candidato)
                .status(StatusCandidatura.EM_ANALISE)
                .build();
        when(candidaturaRepository.save(any(Candidatura.class))).thenReturn(candidaturaSalva);

        MockMultipartFile file = new MockMultipartFile(
                "arquivo", "curriculo2.pdf", "application/pdf",
                "%PDF-1.7\nConteudo".getBytes(StandardCharsets.UTF_8)
        );

        UploadResponseDTO response = candidaturaService.submeterCandidatura(vagaId, file);

        assertNotNull(response);
        assertEquals(candidaturaId, response.getCandidaturaId());
        assertEquals(StatusCandidatura.EM_ANALISE, response.getStatus());
        verify(candidaturaRepository).save(any(Candidatura.class));
        verify(asyncAiProcessingService).processarCurriculoAssincrono(any());
    }

    @Test
    @DisplayName("RN03: Deve submeter com sucesso, salvar com status EM_ANALISE e disparar processamento assíncrono")
    void deveSubmeterCandidaturaComSucesso() {
        when(securityUtils.getCurrentUser()).thenReturn(candidato);
        when(vagaRepository.findById(vagaId)).thenReturn(Optional.of(vaga));
        when(candidaturaRepository.existsByVagaIdAndCandidatoId(vaga.getId(), candidato.getId())).thenReturn(false);

        FileValidationAndStorageService.StoredFileResult storedResult = new FileValidationAndStorageService.StoredFileResult(
                "/tmp/storage/arquivo.pdf", "curriculo.pdf", "uuid.pdf", 1024L
        );
        when(fileStorageService.validateAndStore(any())).thenReturn(storedResult);

        UUID candidaturaId = UUID.randomUUID();
        Candidatura candidaturaSalva = Candidatura.builder()
                .id(candidaturaId)
                .vaga(vaga)
                .candidato(candidato)
                .caminhoCurriculoPdf(storedResult.fullPath())
                .nomeOriginalArquivo(storedResult.originalFilename())
                .tamanhoArquivoBytes(storedResult.sizeBytes())
                .status(StatusCandidatura.EM_ANALISE)
                .build();

        when(candidaturaRepository.save(any(Candidatura.class))).thenReturn(candidaturaSalva);

        MockMultipartFile file = new MockMultipartFile(
                "arquivo", "curriculo.pdf", "application/pdf",
                "%PDF-1.7\nConteudo".getBytes(StandardCharsets.UTF_8)
        );

        UploadResponseDTO response = candidaturaService.submeterCandidatura(vagaId, file);

        assertNotNull(response);
        assertEquals(StatusCandidatura.EM_ANALISE, response.getStatus());
        assertEquals(candidaturaId, response.getCandidaturaId());

        // Verifica que o processamento assíncrono foi disparado
        verify(asyncAiProcessingService, times(1)).processarCurriculoAssincrono(candidaturaId);
    }

    @Test
    @DisplayName("Deve selecionar para entrevista, alterar status para SELECIONADO_ENTREVISTA e gravar auditoria LGPD")
    void deveSelecionarParaEntrevistaEGravarAuditoria() {
        UUID candidaturaId = UUID.randomUUID();
        Candidatura candidatura = Candidatura.builder()
                .id(candidaturaId)
                .vaga(vaga)
                .candidato(candidato)
                .status(StatusCandidatura.CONCLUIDO)
                .build();

        when(candidaturaRepository.findByIdWithDetails(candidaturaId)).thenReturn(Optional.of(candidatura));
        when(securityUtils.getCurrentUser()).thenReturn(recrutador);
        when(candidaturaRepository.save(any(Candidatura.class))).thenAnswer(i -> i.getArgument(0));

        SelecaoEntrevistaDTO dto = new SelecaoEntrevistaDTO("Excelente aderência aos requisitos técnicos");
        CandidaturaDetailDTO result = candidaturaService.selecionarParaEntrevista(candidaturaId, dto);

        assertNotNull(result);
        assertEquals(StatusCandidatura.SELECIONADO_ENTREVISTA, result.getStatus());
        verify(auditoriaRepository, times(1)).save(any());
    }
    @Test
    @DisplayName("LGPD: Deve bloquear download de PDF para recrutador se o candidato ainda não foi selecionado para entrevista")
    void deveBloquearDownloadPdfParaRecrutadorAntesDaEntrevista() {
        UUID candidaturaId = UUID.randomUUID();
        Candidatura candidatura = Candidatura.builder()
                .id(candidaturaId)
                .vaga(vaga)
                .candidato(candidato)
                .status(StatusCandidatura.EM_ANALISE)
                .caminhoCurriculoPdf("/tmp/storage/curriculo.pdf")
                .build();

        when(candidaturaRepository.findByIdWithDetails(candidaturaId)).thenReturn(Optional.of(candidatura));
        when(securityUtils.getCurrentUser()).thenReturn(recrutador);

        assertThrows(BusinessException.class, () ->
                candidaturaService.baixarCurriculoPdf(candidaturaId));

        verify(fileStorageService, never()).loadAsResource(any());
    }

    @Test
    @DisplayName("LGPD: Deve liberar download de PDF para recrutador quando status for SELECIONADO_ENTREVISTA")
    void deveLiberarDownloadPdfParaRecrutadorAposSelecaoEntrevista() {
        UUID candidaturaId = UUID.randomUUID();
        Candidatura candidatura = Candidatura.builder()
                .id(candidaturaId)
                .vaga(vaga)
                .candidato(candidato)
                .status(StatusCandidatura.SELECIONADO_ENTREVISTA)
                .caminhoCurriculoPdf("/tmp/storage/curriculo.pdf")
                .build();

        when(candidaturaRepository.findByIdWithDetails(candidaturaId)).thenReturn(Optional.of(candidatura));
        when(securityUtils.getCurrentUser()).thenReturn(recrutador);
        when(fileStorageService.loadAsResource("/tmp/storage/curriculo.pdf")).thenReturn(mock(Resource.class));

        Resource resource = candidaturaService.baixarCurriculoPdf(candidaturaId);

        assertNotNull(resource);
        verify(fileStorageService, times(1)).loadAsResource("/tmp/storage/curriculo.pdf");
    }

    @Test
    @DisplayName("LGPD: Candidato dono da candidatura pode baixar seu próprio currículo em qualquer status")
    void devePermitirCandidatoBaixarProprioCurriculoEmQualquerStatus() {
        UUID candidaturaId = UUID.randomUUID();
        Candidatura candidatura = Candidatura.builder()
                .id(candidaturaId)
                .vaga(vaga)
                .candidato(candidato)
                .status(StatusCandidatura.EM_ANALISE)
                .caminhoCurriculoPdf("/tmp/storage/curriculo.pdf")
                .build();

        when(candidaturaRepository.findByIdWithDetails(candidaturaId)).thenReturn(Optional.of(candidatura));
        when(securityUtils.getCurrentUser()).thenReturn(candidato);
        when(fileStorageService.loadAsResource("/tmp/storage/curriculo.pdf")).thenReturn(mock(Resource.class));

        Resource resource = candidaturaService.baixarCurriculoPdf(candidaturaId);

        assertNotNull(resource);
        verify(fileStorageService, times(1)).loadAsResource("/tmp/storage/curriculo.pdf");
    }
}
