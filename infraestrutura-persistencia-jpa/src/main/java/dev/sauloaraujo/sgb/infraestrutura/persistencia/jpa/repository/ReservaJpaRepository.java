package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.ReservaJpa;

/**
 * Repositório Spring Data JPA para Reserva.
 */
@Repository
public interface ReservaJpaRepository extends JpaRepository<ReservaJpa, String> {
}

