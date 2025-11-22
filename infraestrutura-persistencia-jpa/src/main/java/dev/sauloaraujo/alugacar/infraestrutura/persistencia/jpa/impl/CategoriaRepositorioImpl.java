package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.CategoriaJpa;
import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.repositories.CategoriaJpaRepository;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaRepositorio;

/**
 * Implementação JPA do repositório de Categorias.
 */
@Repository
public class CategoriaRepositorioImpl implements CategoriaRepositorio {

	@Autowired
	private CategoriaJpaRepository jpaRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public void salvar(Categoria categoria) {
		CategoriaJpa categoriaJpa = modelMapper.map(categoria, CategoriaJpa.class);
		jpaRepository.save(categoriaJpa);
	}

	@Override
	public Optional<Categoria> buscarPorCodigo(CategoriaCodigo codigo) {
		return jpaRepository.findById(codigo.name())
				.map(jpa -> modelMapper.map(jpa, Categoria.class));
	}

	@Override
	public List<Categoria> listarTodas() {
		return jpaRepository.findAll().stream()
				.map(jpa -> modelMapper.map(jpa, Categoria.class))
				.collect(Collectors.toList());
	}
}
