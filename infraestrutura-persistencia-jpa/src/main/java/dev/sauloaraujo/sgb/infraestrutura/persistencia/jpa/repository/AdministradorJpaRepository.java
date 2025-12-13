package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.AdministradorJpa;

public interface AdministradorJpaRepository extends JpaRepository<AdministradorJpa, String> {
	Optional<AdministradorJpa> findByLogin(String login);
}

