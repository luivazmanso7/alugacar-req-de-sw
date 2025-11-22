package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.ReservaJpa;
import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.repositories.ReservaJpaRepository;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;

/**
 * Implementação JPA do repositório de Reservas.
 */
@Repository
public class ReservaRepositorioImpl implements ReservaRepositorio {

	@Autowired
	private ReservaJpaRepository jpaRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public void salvar(Reserva reserva) {
		ReservaJpa reservaJpa = modelMapper.map(reserva, ReservaJpa.class);
		jpaRepository.save(reservaJpa);
	}

	@Override
	public Optional<Reserva> buscarPorCodigo(String codigo) {
		return jpaRepository.findById(codigo)
				.map(jpa -> modelMapper.map(jpa, Reserva.class));
	}

	@Override
	public List<Reserva> listar() {
		return jpaRepository.findAll().stream()
				.map(jpa -> modelMapper.map(jpa, Reserva.class))
				.collect(Collectors.toList());
	}
}
