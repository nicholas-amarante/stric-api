package com.stric.recrutamento.domain.repositories;

import com.stric.recrutamento.domain.entities.AnaliseIA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnaliseIARepository extends JpaRepository<AnaliseIA, UUID> {

    Optional<AnaliseIA> findByCandidaturaId(UUID candidaturaId);
}
