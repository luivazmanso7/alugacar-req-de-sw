package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities;

import java.math.BigDecimal;

import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusLocacao;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidade JPA para persistência de Locações.
 */
@Entity
@Table(name = "LOCACAO")
public class LocacaoJpa {

	@Id
	@Column(name = "codigo", nullable = false, length = 50)
	private String codigo;

	@ManyToOne
	@JoinColumn(name = "reserva_codigo", nullable = false)
	private ReservaJpa reserva;

	@ManyToOne
	@JoinColumn(name = "veiculo_placa", nullable = false)
	private VeiculoJpa veiculo;

	@Column(name = "dias_previstos", nullable = false)
	private int diasPrevistos;

	@Column(name = "valor_diaria", nullable = false, precision = 10, scale = 2)
	private BigDecimal valorDiaria;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private StatusLocacao status;

	@Embedded
	private ChecklistVistoriaJpa vistoriaRetirada;

	@Embedded
	private ChecklistVistoriaJpa vistoriaDevolucao;

	protected LocacaoJpa() {
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public ReservaJpa getReserva() {
		return reserva;
	}

	public void setReserva(ReservaJpa reserva) {
		this.reserva = reserva;
	}

	public VeiculoJpa getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(VeiculoJpa veiculo) {
		this.veiculo = veiculo;
	}

	public int getDiasPrevistos() {
		return diasPrevistos;
	}

	public void setDiasPrevistos(int diasPrevistos) {
		this.diasPrevistos = diasPrevistos;
	}

	public BigDecimal getValorDiaria() {
		return valorDiaria;
	}

	public void setValorDiaria(BigDecimal valorDiaria) {
		this.valorDiaria = valorDiaria;
	}

	public StatusLocacao getStatus() {
		return status;
	}

	public void setStatus(StatusLocacao status) {
		this.status = status;
	}

	public ChecklistVistoriaJpa getVistoriaRetirada() {
		return vistoriaRetirada;
	}

	public void setVistoriaRetirada(ChecklistVistoriaJpa vistoriaRetirada) {
		this.vistoriaRetirada = vistoriaRetirada;
	}

	public ChecklistVistoriaJpa getVistoriaDevolucao() {
		return vistoriaDevolucao;
	}

	public void setVistoriaDevolucao(ChecklistVistoriaJpa vistoriaDevolucao) {
		this.vistoriaDevolucao = vistoriaDevolucao;
	}
}
