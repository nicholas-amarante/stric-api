package com.stric.recrutamento.controller;

import com.stric.recrutamento.dto.common.ApiResponse;
import com.stric.recrutamento.dto.vaga.VagaCreateDTO;
import com.stric.recrutamento.dto.vaga.VagaResponseDTO;
import com.stric.recrutamento.dto.vaga.VagaStatusUpdateDTO;
import com.stric.recrutamento.dto.vaga.VagaUpdateDTO;
import com.stric.recrutamento.service.VagaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vagas")
@RequiredArgsConstructor
@Tag(name = "2. Gestão de Vagas", description = "Endpoints para cadastro, edição, consulta e controle de status de vagas")
public class VagaController {

    private final VagaService vagaService;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Criar nova vaga (RN01)", description = "Permite que um recrutador cadastre uma vaga com requisitos obrigatórios, desejáveis e experiência mínima.")
    public ResponseEntity<ApiResponse<VagaResponseDTO>> criarVaga(@Valid @RequestBody VagaCreateDTO dto) {
        VagaResponseDTO response = vagaService.criarVaga(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Vaga cadastrada com sucesso.", response));
    }

    @GetMapping
    @Operation(summary = "Listar vagas abertas", description = "Lista todas as vagas de emprego ativas com status ABERTA. Permite filtragem por termo de busca e experiǦncia mǭxima.")
    public ResponseEntity<ApiResponse<List<VagaResponseDTO>>> listarVagasAbertas(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer maxExperience) {
        List<VagaResponseDTO> vagas = vagaService.listarVagasAbertas(search, maxExperience);
        return ResponseEntity.ok(ApiResponse.ok(vagas));
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Listar vagas do recrutador logado", description = "Retorna todas as vagas gerenciadas pelo recrutador autenticado.")
    public ResponseEntity<ApiResponse<List<VagaResponseDTO>>> listarMinhasVagas() {
        List<VagaResponseDTO> vagas = vagaService.listarMinhasVagasRecrutador();
        return ResponseEntity.ok(ApiResponse.ok(vagas));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de uma vaga", description = "Retorna os dados completos de uma vaga pelo seu identificador UUID.")
    public ResponseEntity<ApiResponse<VagaResponseDTO>> obterPorId(@PathVariable UUID id) {
        VagaResponseDTO vaga = vagaService.obterPorId(id);
        return ResponseEntity.ok(ApiResponse.ok(vaga));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Atualizar vaga", description = "Permite ao recrutador responsável editar os dados de uma vaga.")
    public ResponseEntity<ApiResponse<VagaResponseDTO>> atualizarVaga(
            @PathVariable UUID id,
            @Valid @RequestBody VagaUpdateDTO dto) {
        VagaResponseDTO vaga = vagaService.atualizarVaga(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Vaga atualizada com sucesso.", vaga));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Alterar status da vaga", description = "Altera o status da vaga para ABERTA, PAUSADA ou ENCERRADA.")
    public ResponseEntity<ApiResponse<VagaResponseDTO>> atualizarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody VagaStatusUpdateDTO dto) {
        VagaResponseDTO vaga = vagaService.atualizarStatus(id, dto.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("Status da vaga atualizado com sucesso.", vaga));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Excluir vaga", description = "Remove a vaga informada do sistema.")
    public ResponseEntity<ApiResponse<Void>> excluirVaga(@PathVariable UUID id) {
        vagaService.excluirVaga(id);
        return ResponseEntity.ok(ApiResponse.ok("Vaga excluída com sucesso.", null));
    }
}
