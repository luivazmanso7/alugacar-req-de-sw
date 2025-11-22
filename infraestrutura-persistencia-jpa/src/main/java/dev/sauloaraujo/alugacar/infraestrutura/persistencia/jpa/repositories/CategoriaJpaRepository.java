package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.CategoriaJpa;

/**
 * Repositório Spring Data JPA para CategoriaJpa.
 */
public interface CategoriaJpaRepository extends JpaRepository<CategoriaJpa, String> {
}
