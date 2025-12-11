package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.CategoriaJpa;

/**
 * Repositório Spring Data JPA para Categoria.
 */
@Repository
public interface CategoriaJpaRepository extends JpaRepository<CategoriaJpa, String> {
}

