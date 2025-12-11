package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaRepositorio;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.JpaMapeador;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA para persistência de Categorias de Veículos.
 */
@Entity
@Table(name = "CATEGORIA")
public class CategoriaJpa {

	@Id
	@Column(name = "codigo", nullable = false, length = 20)
	private String codigo;

	@Column(name = "nome", nullable = false, length = 100)
	private String nome;

	@Column(name = "descricao", length = 500)
	private String descricao;

	@Column(name = "diaria", nullable = false, precision = 10, scale = 2)
	private BigDecimal diaria;

	@Column(name = "modelos_exemplo", length = 500)
	private String modelosExemplo;

	@Column(name = "quantidade_disponivel", nullable = false)
	private int quantidadeDisponivel;

	public CategoriaJpa() {
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getDiaria() {
		return diaria;
	}

	public void setDiaria(BigDecimal diaria) {
		this.diaria = diaria;
	}

	public String getModelosExemplo() {
		return modelosExemplo;
	}

	public void setModelosExemplo(String modelosExemplo) {
		this.modelosExemplo = modelosExemplo;
	}

	public int getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}

	public void setQuantidadeDisponivel(int quantidadeDisponivel) {
		this.quantidadeDisponivel = quantidadeDisponivel;
	}
}

interface CategoriaJpaRepository extends JpaRepository<CategoriaJpa, String> { // package-private
}

@Repository
class CategoriaRepositorioImpl implements CategoriaRepositorio { // implementação exposta

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
		return repositorio.findById(codigo.name())
				.map(jpa -> mapeador.map(jpa, Categoria.class));
	}

	@Override
	public List<Categoria> listarTodas() {
		var categoriasJpa = repositorio.findAll();
		return categoriasJpa.stream()
				.map(jpa -> mapeador.map(jpa, Categoria.class))
				.toList();
	}
}
