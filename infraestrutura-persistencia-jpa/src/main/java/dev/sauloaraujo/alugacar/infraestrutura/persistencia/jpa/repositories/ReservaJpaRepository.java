package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.ReservaJpa;

/**
 * Repositório Spring Data JPA para ReservaJpa.
 */
public interface ReservaJpaRepository extends JpaRepository<ReservaJpa, String> {
}
