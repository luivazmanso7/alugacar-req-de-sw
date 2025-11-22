package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaRepositorio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA para persistência de Categorias de Veículos.
 */
@Entity
@Table(name = "CATEGORIA")
class CategoriaJpa {

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "codigo", nullable = false, length = 20)
	CategoriaCodigo codigo;

	@Column(name = "nome", nullable = false, length = 100)
	String nome;

	@Column(name = "descricao", length = 500)
	String descricao;

	@Column(name = "diaria", nullable = false, precision = 10, scale = 2)
	BigDecimal diaria;

	@Column(name = "modelos_exemplo", length = 500)
	String modelosExemplo;

	@Column(name = "quantidade_disponivel", nullable = false)
	int quantidadeDisponivel;
}

/**
 * Repositório Spring Data JPA para Categoria.
 */
interface CategoriaJpaRepository extends JpaRepository<CategoriaJpa, CategoriaCodigo> {
}

/**
 * Implementação do repositório do domínio para Categoria.
 */
@Repository
class CategoriaRepositorioImpl implements CategoriaRepositorio {

	@Autowired
	CategoriaJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Categoria categoria) {
		var categoriaJpa = mapeador.map(categoria, CategoriaJpa.class);
		repositorio.save(categoriaJpa);
	}

	@Override
	public Optional<Categoria> buscarPorCodigo(CategoriaCodigo codigo) {
		return repositorio.findById(codigo)
				.map(jpa -> mapeador.map(jpa, Categoria.class));
	}

	@Override
	public List<Categoria> listarTodas() {
		var categorias = repositorio.findAll();
		return mapeador.mapList(categorias, Categoria.class);
	}
}
