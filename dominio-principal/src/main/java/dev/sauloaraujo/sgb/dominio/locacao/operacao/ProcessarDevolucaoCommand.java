package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ProcessarDevolucaoCommand {
	private final String codigoLocacao;
	private final int quilometragem;
	private final String combustivel;
	private final boolean possuiAvarias;
	private final BigDecimal taxaCombustivel;
	private final LocalDateTime dataDevolucao;
	private final BigDecimal percentualMultaAtraso;

	private ProcessarDevolucaoCommand(Builder builder) {
		this.codigoLocacao = Objects.requireNonNull(builder.codigoLocacao, "O código da locação é obrigatório");
		this.quilometragem = builder.quilometragem;
		this.combustivel = Objects.requireNonNull(builder.combustivel, "O nível de combustível é obrigatório");
		this.possuiAvarias = builder.possuiAvarias;
		this.taxaCombustivel = builder.taxaCombustivel;
		this.dataDevolucao = Objects.requireNonNull(builder.dataDevolucao, "A data de devolução é obrigatória");
		this.percentualMultaAtraso = Objects.requireNonNull(builder.percentualMultaAtraso, "O percentual de multa por atraso é obrigatório");
	}

	public String getCodigoLocacao() {
		return codigoLocacao;
	}

	public int getQuilometragem() {
		return quilometragem;
	}

	public String getCombustivel() {
		return combustivel;
	}

	public boolean isPossuiAvarias() {
		return possuiAvarias;
	}

	public BigDecimal getTaxaCombustivel() {
		return taxaCombustivel;
	}

	public LocalDateTime getDataDevolucao() {
		return dataDevolucao;
	}

	public BigDecimal getPercentualMultaAtraso() {
		return percentualMultaAtraso;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String codigoLocacao;
		private int quilometragem;
		private String combustivel = "CHEIO";
		private boolean possuiAvarias;
		private BigDecimal taxaCombustivel = BigDecimal.ZERO;
		private LocalDateTime dataDevolucao = LocalDateTime.now();
		private BigDecimal percentualMultaAtraso = new BigDecimal("0.10"); // 10% padrão

		public Builder codigoLocacao(String codigoLocacao) {
			this.codigoLocacao = codigoLocacao;
			return this;
		}

		public Builder quilometragem(int quilometragem) {
			this.quilometragem = quilometragem;
			return this;
		}

		public Builder combustivel(String combustivel) {
			this.combustivel = combustivel;
			return this;
		}

		public Builder possuiAvarias(boolean possuiAvarias) {
			this.possuiAvarias = possuiAvarias;
			return this;
		}

		public Builder taxaCombustivel(BigDecimal taxaCombustivel) {
			this.taxaCombustivel = taxaCombustivel;
			return this;
		}

		public Builder dataDevolucao(LocalDateTime dataDevolucao) {
			this.dataDevolucao = dataDevolucao;
			return this;
		}

		public Builder percentualMultaAtraso(BigDecimal percentualMultaAtraso) {
			this.percentualMultaAtraso = percentualMultaAtraso;
			return this;
		}

		public ProcessarDevolucaoCommand build() {
			return new ProcessarDevolucaoCommand(this);
		}
	}
}
