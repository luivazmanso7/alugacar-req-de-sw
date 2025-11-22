package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.VeiculoJpa;
import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.repositories.VeiculoJpaRepository;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;

/**
 * Implementação JPA do repositório de Veículos.
 * Responsável por fazer a ponte entre o domínio e a camada de persistência.
 */
@Repository
public class VeiculoRepositorioImpl implements VeiculoRepositorio {

	@Autowired
	private VeiculoJpaRepository jpaRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public void salvar(Veiculo veiculo) {
		VeiculoJpa veiculoJpa = modelMapper.map(veiculo, VeiculoJpa.class);
		jpaRepository.save(veiculoJpa);
	}

	@Override
	public Optional<Veiculo> buscarPorPlaca(String placa) {
		return jpaRepository.findById(placa)
				.map(jpa -> modelMapper.map(jpa, Veiculo.class));
	}

	@Override
	public List<Veiculo> buscarDisponiveis(String cidade, CategoriaCodigo categoria) {
		List<VeiculoJpa> veiculosJpa = jpaRepository.findDisponiveisPorCidadeECategoria(
				cidade,
				categoria.name(),
				StatusVeiculo.DISPONIVEL
		);

		return veiculosJpa.stream()
				.map(jpa -> modelMapper.map(jpa, Veiculo.class))
				.collect(Collectors.toList());
	}

	@Override
	public List<Veiculo> buscarDisponiveis(String cidade) {
		List<VeiculoJpa> veiculosJpa = jpaRepository.findDisponiveisPorCidade(
				cidade,
				StatusVeiculo.DISPONIVEL
		);

		return veiculosJpa.stream()
				.map(jpa -> modelMapper.map(jpa, Veiculo.class))
				.collect(Collectors.toList());
	}
}
