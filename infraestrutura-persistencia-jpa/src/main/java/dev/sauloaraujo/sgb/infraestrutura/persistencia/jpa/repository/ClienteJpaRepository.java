package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.ClienteJpa;

/**
 * Repositório Spring Data JPA para Cliente.
 */
@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteJpa, String> {
	Optional<ClienteJpa> findByLogin(String login);
}

