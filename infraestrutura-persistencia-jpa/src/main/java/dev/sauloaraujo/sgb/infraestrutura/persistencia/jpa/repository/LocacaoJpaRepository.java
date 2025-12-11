package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusLocacao;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.LocacaoJpa;

/**
 * Repositório Spring Data JPA para Locação.
 */
@Repository
public interface LocacaoJpaRepository extends JpaRepository<LocacaoJpa, String> {
    
    /**
     * Busca uma locação pelo código.
     */
    @Query("SELECT l FROM LocacaoJpa l " +
           "LEFT JOIN FETCH l.reserva r " +
           "LEFT JOIN FETCH r.cliente " +
           "LEFT JOIN FETCH l.veiculo " +
           "WHERE l.codigo = :codigo")
    Optional<LocacaoJpa> findByCodigo(@Param("codigo") String codigo);
    
    /**
     * Busca uma locação ativa pelo código (status = 'ATIVA').
     */
    @Query("SELECT l FROM LocacaoJpa l " +
           "LEFT JOIN FETCH l.reserva r " +
           "LEFT JOIN FETCH r.cliente " +
           "LEFT JOIN FETCH l.veiculo " +
           "WHERE l.codigo = :codigo AND l.status = 'ATIVA'")
    Optional<LocacaoJpa> findByCodigoAndStatusAtiva(@Param("codigo") String codigo);
    
    /**
     * Busca locações por status.
     */
    List<LocacaoJpa> findByStatus(StatusLocacao status);
    
    /**
     * Busca locações por cliente (navega pelas propriedades: Reserva -> Cliente -> CpfOuCnpj).
     */
    List<LocacaoJpa> findByReserva_Cliente_CpfOuCnpj(String cpfOuCnpj);
}
