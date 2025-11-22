package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.LocacaoJpa;

/**
 * Repositório Spring Data JPA para LocacaoJpa.
 */
public interface LocacaoJpaRepository extends JpaRepository<LocacaoJpa, String> {
}
