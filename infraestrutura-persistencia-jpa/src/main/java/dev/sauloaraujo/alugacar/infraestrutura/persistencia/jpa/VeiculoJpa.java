package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA para persistência de Veículos.
 */
@Entity
@Table(name = "VEICULO")
class VeiculoJpa {

	@Id
	@Column(name = "placa", nullable = false, length = 10)
	String placa;

	@Column(name = "modelo", nullable = false, length = 100)
	String modelo;

	@Enumerated(EnumType.STRING)
	@Column(name = "categoria", nullable = false, length = 20)
	CategoriaCodigo categoria;

	@Column(name = "cidade", nullable = false, length = 100)
	String cidade;

	@Column(name = "diaria", nullable = false, precision = 10, scale = 2)
	BigDecimal diaria;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	StatusVeiculo status;

	@Column(name = "manutencao_prevista")
	LocalDateTime manutencaoPrevista;

	@Column(name = "manutencao_nota", length = 500)
	String manutencaoNota;

	@Embedded
	PatioJpa patio;
}

/**
 * Repositório Spring Data JPA para Veículo.
 */
interface VeiculoJpaRepository extends JpaRepository<VeiculoJpa, String> {

	@Query("SELECT v FROM VeiculoJpa v WHERE v.cidade = :cidade AND v.categoria = :categoria AND v.status = 'DISPONIVEL'")
	List<VeiculoJpa> findDisponiveis(@Param("cidade") String cidade, @Param("categoria") CategoriaCodigo categoria);

	@Query("SELECT v FROM VeiculoJpa v WHERE v.cidade = :cidade AND v.status = 'DISPONIVEL'")
	List<VeiculoJpa> findDisponiveisPorCidade(@Param("cidade") String cidade);
}

/**
 * Implementação do repositório do domínio para Veículo.
 */
@Repository
class VeiculoRepositorioImpl implements VeiculoRepositorio {

	@Autowired
	VeiculoJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Veiculo veiculo) {
		var veiculoJpa = mapeador.map(veiculo, VeiculoJpa.class);
		repositorio.save(veiculoJpa);
	}

	@Override
	public Optional<Veiculo> buscarPorPlaca(String placa) {
		return repositorio.findById(placa)
				.map(jpa -> mapeador.map(jpa, Veiculo.class));
	}

	@Override
	public List<Veiculo> buscarDisponiveis(String cidade, CategoriaCodigo categoria) {
		var veiculos = repositorio.findDisponiveis(cidade, categoria);
		return mapeador.mapList(veiculos, Veiculo.class);
	}

	@Override
	public List<Veiculo> buscarDisponiveis(String cidade) {
		var veiculos = repositorio.findDisponiveisPorCidade(cidade);
		return mapeador.mapList(veiculos, Veiculo.class);
	}
}
