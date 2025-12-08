package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object JPA para Período de Locação (embutido em Reserva).
 */
@Embeddable
class PeriodoLocacaoJpa {

	@Column(name = "data_retirada", nullable = false)
	private LocalDateTime retirada;

	@Column(name = "data_devolucao", nullable = false)
	private LocalDateTime devolucao;

	public PeriodoLocacaoJpa() {
	}

	public LocalDateTime getRetirada() {
		return retirada;
	}

	public void setRetirada(LocalDateTime retirada) {
		this.retirada = retirada;
	}

	public LocalDateTime getDevolucao() {
		return devolucao;
	}

	public void setDevolucao(LocalDateTime devolucao) {
		this.devolucao = devolucao;
	}
}
