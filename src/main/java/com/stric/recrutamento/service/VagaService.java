package com.stric.recrutamento.service;

import com.stric.recrutamento.domain.entities.Usuario;
import com.stric.recrutamento.domain.entities.Vaga;
import com.stric.recrutamento.domain.enums.Role;
import com.stric.recrutamento.domain.enums.StatusVaga;
import com.stric.recrutamento.domain.repositories.VagaRepository;
import com.stric.recrutamento.dto.vaga.VagaCreateDTO;
import com.stric.recrutamento.dto.vaga.VagaResponseDTO;
import com.stric.recrutamento.dto.vaga.VagaUpdateDTO;
import com.stric.recrutamento.exception.BusinessException;
import com.stric.recrutamento.exception.ResourceNotFoundException;
import com.stric.recrutamento.exception.UnauthorizedAccessException;
import com.stric.recrutamento.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VagaService {

    private final VagaRepository vagaRepository;
    private final SecurityUtils securityUtils;

    public VagaService(VagaRepository vagaRepository, SecurityUtils securityUtils) {
        this.vagaRepository = vagaRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public VagaResponseDTO criarVaga(VagaCreateDTO dto) {
        Usuario recrutador = securityUtils.getCurrentUser();

        if (recrutador.getRole() != Role.ROLE_RECRUITER) {
            throw new UnauthorizedAccessException("Apenas recrutadores podem cadastrar vagas de emprego.");
        }

        Vaga vaga = Vaga.builder()
                .titulo(dto.getTitulo().trim())
                .descricao(dto.getDescricao().trim())
                .requisitosObrigatorios(dto.getRequisitosObrigatorios())
                .requisitosDesejaveis(dto.getRequisitosDesejaveis())
                .tempoMinimoExperienciaAnos(dto.getTempoMinimoExperienciaAnos() != null ? dto.getTempoMinimoExperienciaAnos() : 0)
                .status(StatusVaga.ABERTA)
                .recrutador(recrutador)
                .build();

        vaga = vagaRepository.save(vaga);
        log.info("Vaga criada com sucesso: ID={}, Título='{}', Recrutador={}",
                vaga.getId(), vaga.getTitulo(), recrutador.getEmail());

        return VagaResponseDTO.fromEntity(vaga);
    }

    @Transactional
    public VagaResponseDTO atualizarVaga(UUID id, VagaUpdateDTO dto) {
        Vaga vaga = findVagaOrThrow(id);
        validarPropriedadeVaga(vaga);

        vaga.setTitulo(dto.getTitulo().trim());
        vaga.setDescricao(dto.getDescricao().trim());
        vaga.setRequisitosObrigatorios(dto.getRequisitosObrigatorios());
        vaga.setRequisitosDesejaveis(dto.getRequisitosDesejaveis());
        vaga.setTempoMinimoExperienciaAnos(dto.getTempoMinimoExperienciaAnos() != null ? dto.getTempoMinimoExperienciaAnos() : 0);

        if (dto.getStatus() != null) {
            vaga.setStatus(dto.getStatus());
        }

        vaga = vagaRepository.save(vaga);
        log.info("Vaga atualizada: ID={}, Título='{}'", vaga.getId(), vaga.getTitulo());

        return VagaResponseDTO.fromEntity(vaga);
    }

    @Transactional
    public VagaResponseDTO atualizarStatus(UUID id, StatusVaga status) {
        Vaga vaga = findVagaOrThrow(id);
        validarPropriedadeVaga(vaga);

        vaga.setStatus(status);
        vaga = vagaRepository.save(vaga);
        log.info("Status da vaga ID={} alterado para {}", id, status);

        return VagaResponseDTO.fromEntity(vaga);
    }

    @Transactional(readOnly = true)
    public VagaResponseDTO obterPorId(UUID id) {
        Vaga vaga = findVagaOrThrow(id);
        return VagaResponseDTO.fromEntity(vaga);
    }

    @Transactional(readOnly = true)
    public List<VagaResponseDTO> listarVagasAbertas(String search, Integer maxExperience) {
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return vagaRepository.findVagasAbertasComFiltro(StatusVaga.ABERTA, searchTerm, maxExperience).stream()
                .map(VagaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VagaResponseDTO> listarMinhasVagasRecrutador() {
        Usuario recrutador = securityUtils.getCurrentUser();
        return vagaRepository.findByRecrutadorId(recrutador.getId()).stream()
                .map(VagaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void excluirVaga(UUID id) {
        Vaga vaga = findVagaOrThrow(id);
        validarPropriedadeVaga(vaga);

        vagaRepository.delete(vaga);
        log.info("Vaga excluída com sucesso: ID={}", id);
    }

    public Vaga findVagaOrThrow(UUID id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga", id));
    }

    private void validarPropriedadeVaga(Vaga vaga) {
        Usuario usuarioLogado = securityUtils.getCurrentUser();
        if (!vaga.getRecrutador().getId().equals(usuarioLogado.getId())) {
            throw new UnauthorizedAccessException("Você não tem permissão para alterar ou gerenciar uma vaga criada por outro recrutador.");
        }
    }
}
