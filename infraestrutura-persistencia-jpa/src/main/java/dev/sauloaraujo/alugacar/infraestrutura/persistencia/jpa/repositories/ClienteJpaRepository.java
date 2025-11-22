package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.ClienteJpa;

/**
 * Repositório Spring Data JPA para ClienteJpa.
 */
public interface ClienteJpaRepository extends JpaRepository<ClienteJpa, String> {
}
