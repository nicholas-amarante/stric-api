package com.stric.recrutamento.domain.repositories;

import com.stric.recrutamento.domain.entities.AuditoriaAcessoContato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditoriaAcessoContatoRepository extends JpaRepository<AuditoriaAcessoContato, UUID> {

    @Query("SELECT a FROM AuditoriaAcessoContato a " +
           "LEFT JOIN FETCH a.recrutador " +
           "WHERE a.candidatura.id = :candidaturaId " +
           "ORDER BY a.dataAcesso DESC")
    List<AuditoriaAcessoContato> findByCandidaturaId(@Param("candidaturaId") UUID candidaturaId);
}
