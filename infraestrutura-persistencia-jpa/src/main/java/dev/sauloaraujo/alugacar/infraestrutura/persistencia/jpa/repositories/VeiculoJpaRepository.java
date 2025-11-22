package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.VeiculoJpa;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;

/**
 * Repositório Spring Data JPA para VeiculoJpa.
 */
public interface VeiculoJpaRepository extends JpaRepository<VeiculoJpa, String> {

	/**
	 * Busca veículos disponíveis em uma cidade específica e categoria.
	 */
	@Query("SELECT v FROM VeiculoJpa v WHERE v.cidade = :cidade " +
			"AND v.categoria = :categoria AND v.status = :status")
	List<VeiculoJpa> findDisponiveisPorCidadeECategoria(
			@Param("cidade") String cidade,
			@Param("categoria") String categoria,
			@Param("status") StatusVeiculo status);

	/**
	 * Busca veículos disponíveis em uma cidade (todas as categorias).
	 */
	@Query("SELECT v FROM VeiculoJpa v WHERE v.cidade = :cidade AND v.status = :status")
	List<VeiculoJpa> findDisponiveisPorCidade(
			@Param("cidade") String cidade,
			@Param("status") StatusVeiculo status);
}
