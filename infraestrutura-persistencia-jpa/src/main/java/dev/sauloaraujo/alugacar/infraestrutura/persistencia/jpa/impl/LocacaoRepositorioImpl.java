package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.LocacaoJpa;
import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.repositories.LocacaoJpaRepository;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;

/**
 * Implementação JPA do repositório de Locações.
 */
@Repository
public class LocacaoRepositorioImpl implements LocacaoRepositorio {

	@Autowired
	private LocacaoJpaRepository jpaRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public void salvar(Locacao locacao) {
		LocacaoJpa locacaoJpa = modelMapper.map(locacao, LocacaoJpa.class);
		jpaRepository.save(locacaoJpa);
	}

	@Override
	public Optional<Locacao> buscarPorCodigoLocacao(String codigo) {
		return jpaRepository.findById(codigo)
				.map(jpa -> modelMapper.map(jpa, Locacao.class));
	}

	@Override
	public List<Locacao> listarLocacoes() {
		return jpaRepository.findAll().stream()
				.map(jpa -> modelMapper.map(jpa, Locacao.class))
				.collect(Collectors.toList());
	}
}
