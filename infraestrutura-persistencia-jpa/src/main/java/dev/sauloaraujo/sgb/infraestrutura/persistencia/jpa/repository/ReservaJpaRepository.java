package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.ReservaJpa;

/**
 * Repositório Spring Data JPA para Reserva.
 */
@Repository
public interface ReservaJpaRepository extends JpaRepository<ReservaJpa, String> {
	
	/**
	 * Busca todas as reservas de um cliente específico.
	 * 
	 * @param cpfOuCnpj CPF ou CNPJ do cliente
	 * @return lista de reservas do cliente
	 */
	@Query("SELECT r FROM ReservaJpa r WHERE r.cliente.cpfOuCnpj = :cpfOuCnpj ORDER BY r.periodo.retirada DESC")
	List<ReservaJpa> findByClienteCpfOuCnpj(@Param("cpfOuCnpj") String cpfOuCnpj);

	/**
	 * Busca todas as reservas de um veículo específico.
	 * 
	 * @param placaVeiculo placa do veículo
	 * @return lista de reservas do veículo
	 */
	@Query("SELECT r FROM ReservaJpa r WHERE r.placaVeiculo = :placaVeiculo ORDER BY r.periodo.retirada DESC")
	List<ReservaJpa> findByPlacaVeiculo(@Param("placaVeiculo") String placaVeiculo);
}

