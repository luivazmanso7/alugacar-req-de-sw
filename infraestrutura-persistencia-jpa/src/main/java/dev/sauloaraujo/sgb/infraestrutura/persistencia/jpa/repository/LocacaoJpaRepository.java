package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.LocacaoJpa;

/**
 * Repositório Spring Data JPA para Locação.
 */
@Repository
public interface LocacaoJpaRepository extends JpaRepository<LocacaoJpa, Long> {
    
    /**
     * Busca uma locação pelo código.
     */
    @Query("SELECT l FROM LocacaoJpa l " +
           "LEFT JOIN FETCH l.reserva r " +
           "LEFT JOIN FETCH r.veiculo v " +
           "LEFT JOIN FETCH r.cliente c " +
           "WHERE l.codigo = :codigo")
    Optional<LocacaoJpa> findByCodigo(@Param("codigo") String codigo);
    
    /**
     * Busca uma locação ativa pelo código (status = 'ATIVA').
     */
    @Query("SELECT l FROM LocacaoJpa l " +
           "LEFT JOIN FETCH l.reserva r " +
           "LEFT JOIN FETCH r.veiculo v " +
           "LEFT JOIN FETCH r.cliente c " +
           "WHERE l.codigo = :codigo AND l.status = 'ATIVA'")
    Optional<LocacaoJpa> findByCodigoAndStatusAtiva(@Param("codigo") String codigo);
}
