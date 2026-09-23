package com.stric.recrutamento.domain.repositories;

import com.stric.recrutamento.domain.entities.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidaturaRepository extends JpaRepository<Candidatura, UUID> {

    boolean existsByVagaIdAndCandidatoId(UUID vagaId, UUID candidatoId);

    Optional<Candidatura> findByVagaIdAndCandidatoId(UUID vagaId, UUID candidatoId);

    @Query("SELECT c FROM Candidatura c " +
           "LEFT JOIN FETCH c.candidato " +
           "LEFT JOIN FETCH c.vaga " +
           "LEFT JOIN FETCH c.analiseIA a " +
           "WHERE c.vaga.id = :vagaId " +
           "ORDER BY a.scoreAderencia DESC NULLS LAST, c.dataSubmissao DESC")
    List<Candidatura> findByVagaIdWithDetails(@Param("vagaId") UUID vagaId);

    @Query("SELECT c FROM Candidatura c " +
           "LEFT JOIN FETCH c.vaga v " +
           "LEFT JOIN FETCH v.recrutador " +
           "LEFT JOIN FETCH c.analiseIA " +
           "WHERE c.candidato.id = :candidatoId " +
           "ORDER BY c.dataSubmissao DESC")
    List<Candidatura> findByCandidatoIdWithDetails(@Param("candidatoId") UUID candidatoId);

    @Query("SELECT c FROM Candidatura c " +
           "LEFT JOIN FETCH c.candidato " +
           "LEFT JOIN FETCH c.vaga v " +
           "LEFT JOIN FETCH v.recrutador " +
           "LEFT JOIN FETCH c.analiseIA " +
           "WHERE c.id = :id")
    Optional<Candidatura> findByIdWithDetails(@Param("id") UUID id);
}
