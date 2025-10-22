package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import java.util.Objects;
import java.util.Optional;

import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;

public class ConsultaDisponibilidade {
	private final String cidade;
	private final PeriodoLocacao periodo;
	private final CategoriaCodigo categoria;

	private ConsultaDisponibilidade(Builder builder) {
		this.cidade = Objects.requireNonNull(builder.cidade, "A cidade é obrigatória");
		this.periodo = Objects.requireNonNull(builder.periodo, "O período é obrigatório");
		this.categoria = builder.categoria;
	}

	public String getCidade() {
		return cidade;
	}

	public PeriodoLocacao getPeriodo() {
		return periodo;
	}

	public Optional<CategoriaCodigo> getCategoria() {
		return Optional.ofNullable(categoria);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String cidade;
		private PeriodoLocacao periodo;
		private CategoriaCodigo categoria;

		public Builder cidade(String cidade) {
			this.cidade = Objects.requireNonNull(cidade, "A cidade é obrigatória");
			return this;
		}

		public Builder periodo(PeriodoLocacao periodo) {
			this.periodo = Objects.requireNonNull(periodo, "O período é obrigatório");
			return this;
		}

		public Builder categoria(CategoriaCodigo categoria) {
			this.categoria = categoria;
			return this;
		}

		public ConsultaDisponibilidade construir() {
			return new ConsultaDisponibilidade(this);
		}
	}
}
