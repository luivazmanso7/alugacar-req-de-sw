package dev.sauloaraujo.sgb.dominio.locacao.shared;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class PeriodoLocacao {
	private final LocalDateTime retirada;
	private final LocalDateTime devolucao;

	public PeriodoLocacao(LocalDateTime retirada, LocalDateTime devolucao) {
		this.retirada = Objects.requireNonNull(retirada, "A data de retirada é obrigatória");
		this.devolucao = Objects.requireNonNull(devolucao, "A data de devolução é obrigatória");

		if (devolucao.isBefore(retirada)) {
			throw new IllegalArgumentException("A devolução não pode ocorrer antes da retirada");
		}
	}

	public LocalDateTime getRetirada() {
		return retirada;
	}

	public LocalDateTime getDevolucao() {
		return devolucao;
	}

	public long dias() {
		var horas = Duration.between(retirada, devolucao).toHours();
		var dias = horas / 24;
		if (horas % 24 != 0) {
			dias++;
		}

		return Math.max(1, dias);
	}
}
