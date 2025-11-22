package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object JPA para Período de Locação.
 * Embutido em Reserva.
 */
@Embeddable
class PeriodoLocacaoJpa {

	@Column(name = "data_retirada", nullable = false)
	LocalDateTime retirada;

	@Column(name = "data_devolucao", nullable = false)
	LocalDateTime devolucao;
}
