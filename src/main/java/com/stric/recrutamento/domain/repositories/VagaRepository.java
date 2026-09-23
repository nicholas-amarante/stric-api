package com.stric.recrutamento.domain.repositories;

import com.stric.recrutamento.domain.entities.Vaga;
import com.stric.recrutamento.domain.enums.StatusVaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, UUID> {

    List<Vaga> findByStatus(StatusVaga status);

    List<Vaga> findByRecrutadorId(UUID recrutadorId);

    @Query("SELECT v FROM Vaga v LEFT JOIN FETCH v.recrutador WHERE v.status = :status ORDER BY v.dataCriacao DESC")
    List<Vaga> findByStatusWithRecrutador(@Param("status") StatusVaga status);

    @Query("SELECT v FROM Vaga v LEFT JOIN FETCH v.recrutador ORDER BY v.dataCriacao DESC")
    List<Vaga> findAllWithRecrutador();

    @Query(value = "SELECT * FROM vagas v " +
           "WHERE v.status = :#{#status.name()} " +
           "AND (:search IS NULL OR LOWER(v.titulo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(v.descricao) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(v.requisitos_obrigatorios) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(v.requisitos_desejaveis) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:maxExperience IS NULL OR v.tempo_minimo_experiencia_anos <= :maxExperience) " +
           "ORDER BY v.data_criacao DESC", nativeQuery = true)
    List<Vaga> findVagasAbertasComFiltro(@Param("status") StatusVaga status, 
                                         @Param("search") String search, 
                                         @Param("maxExperience") Integer maxExperience);
}
