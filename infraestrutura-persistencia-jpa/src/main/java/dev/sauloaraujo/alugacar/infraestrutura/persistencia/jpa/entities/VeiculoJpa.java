package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class VeiculoJpa {

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

	// Construtor padrão para JPA
	protected VeiculoJpa() {
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
