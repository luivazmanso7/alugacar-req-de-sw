package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.util.Objects;

public class ProcessarRetiradaCommand {
	private final String codigoReserva;
	private final String codigoLocacao;
	private final String placaVeiculo;
	private final boolean documentosValidos;
	private final int quilometragem;
	private final String combustivel;

	private ProcessarRetiradaCommand(Builder builder) {
		this.codigoReserva = Objects.requireNonNull(builder.codigoReserva, "O código da reserva é obrigatório");
		this.codigoLocacao = Objects.requireNonNull(builder.codigoLocacao, "O código da locação é obrigatório");
		this.placaVeiculo = Objects.requireNonNull(builder.placaVeiculo, "A placa é obrigatória");
		this.documentosValidos = builder.documentosValidos;
		this.quilometragem = builder.quilometragem;
		this.combustivel = Objects.requireNonNull(builder.combustivel, "O nível de combustível é obrigatório");
	}

	public String getCodigoReserva() {
		return codigoReserva;
	}

	public String getCodigoLocacao() {
		return codigoLocacao;
	}

	public String getPlacaVeiculo() {
		return placaVeiculo;
	}

	public boolean isDocumentosValidos() {
		return documentosValidos;
	}

	public int getQuilometragem() {
		return quilometragem;
	}

	public String getCombustivel() {
		return combustivel;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String codigoReserva;
		private String codigoLocacao;
		private String placaVeiculo;
		private boolean documentosValidos = true;
		private int quilometragem;
		private String combustivel = "CHEIO";

		public Builder codigoReserva(String codigoReserva) {
			this.codigoReserva = codigoReserva;
			return this;
		}

		public Builder codigoLocacao(String codigoLocacao) {
			this.codigoLocacao = codigoLocacao;
			return this;
		}

		public Builder placaVeiculo(String placaVeiculo) {
			this.placaVeiculo = placaVeiculo;
			return this;
		}

		public Builder documentosValidos(boolean documentosValidos) {
			this.documentosValidos = documentosValidos;
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

		public ProcessarRetiradaCommand build() {
			return new ProcessarRetiradaCommand(this);
		}
	}
}
