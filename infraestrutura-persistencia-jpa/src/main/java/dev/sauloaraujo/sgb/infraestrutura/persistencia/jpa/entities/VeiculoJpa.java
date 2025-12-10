package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa;

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
	private String placa;

	@Column(name = "modelo", nullable = false, length = 100)
	private String modelo;

	@Column(name = "categoria", nullable = false, length = 20)
	private String categoria;

	@Column(name = "cidade", nullable = false, length = 100)
	private String cidade;

	@Column(name = "diaria", nullable = false, precision = 10, scale = 2)
	private BigDecimal diaria;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private StatusVeiculo status;

	@Column(name = "manutencao_prevista")
	private LocalDateTime manutencaoPrevista;

	@Column(name = "manutencao_nota", length = 500)
	private String manutencaoNota;

	@Embedded
	private PatioJpa patio;

	VeiculoJpa() {
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public BigDecimal getDiaria() {
		return diaria;
	}

	public void setDiaria(BigDecimal diaria) {
		this.diaria = diaria;
	}

	public StatusVeiculo getStatus() {
		return status;
	}

	public void setStatus(StatusVeiculo status) {
		this.status = status;
	}

	public LocalDateTime getManutencaoPrevista() {
		return manutencaoPrevista;
	}

	public void setManutencaoPrevista(LocalDateTime manutencaoPrevista) {
		this.manutencaoPrevista = manutencaoPrevista;
	}

	public String getManutencaoNota() {
		return manutencaoNota;
	}

	public void setManutencaoNota(String manutencaoNota) {
		this.manutencaoNota = manutencaoNota;
	}

	public PatioJpa getPatio() {
		return patio;
	}

	public void setPatio(PatioJpa patio) {
		this.patio = patio;
	}
}

interface VeiculoJpaRepository extends JpaRepository<VeiculoJpa, String> {

	@Query("SELECT v FROM VeiculoJpa v WHERE v.cidade = :cidade " +
			"AND v.categoria = :categoria AND v.status = :status")
	List<VeiculoJpa> findDisponiveisPorCidadeECategoria(
			@Param("cidade") String cidade,
			@Param("categoria") String categoria,
			@Param("status") StatusVeiculo status);

	@Query("SELECT v FROM VeiculoJpa v WHERE v.cidade = :cidade AND v.status = :status")
	List<VeiculoJpa> findDisponiveisPorCidade(
			@Param("cidade") String cidade,
			@Param("status") StatusVeiculo status);
}

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
		var veiculosJpa = repositorio.findDisponiveisPorCidadeECategoria(
				cidade,
				categoria.name(),
				StatusVeiculo.DISPONIVEL);

		return mapeador.mapList(veiculosJpa, Veiculo.class);
	}

	@Override
	public List<Veiculo> buscarDisponiveis(String cidade) {
		var veiculosJpa = repositorio.findDisponiveisPorCidade(
				cidade,
				StatusVeiculo.DISPONIVEL);

		return mapeador.mapList(veiculosJpa, Veiculo.class);
	}
}
