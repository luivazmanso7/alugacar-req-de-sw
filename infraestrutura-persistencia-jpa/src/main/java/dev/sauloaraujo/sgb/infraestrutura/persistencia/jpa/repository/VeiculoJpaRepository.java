package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.VeiculoJpa;

/**
 * Repositório Spring Data JPA para Veículo.
 */
@Repository
public interface VeiculoJpaRepository extends JpaRepository<VeiculoJpa, String> {

    /**
     * Busca veículos disponíveis por cidade e categoria.
     * Usa query JPQL com função de string para garantir comparação correta.
     */
    @Query("SELECT v FROM VeiculoJpa v WHERE v.cidade = :cidade " +
           "AND UPPER(v.categoria) = UPPER(:categoria) AND v.status = 'DISPONIVEL'")
    List<VeiculoJpa> findDisponiveisPorCidadeECategoria(
            @Param("cidade") String cidade,
            @Param("categoria") String categoria);

    /**
     * Busca veículos disponíveis por cidade.
     */
    @Query("SELECT v FROM VeiculoJpa v WHERE v.cidade = :cidade AND v.status = 'DISPONIVEL'")
    List<VeiculoJpa> findDisponiveisPorCidade(@Param("cidade") String cidade);
}
