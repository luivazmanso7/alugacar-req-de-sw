package dev.sauloaraujo.sgb.dominio.locacao.shared;

public enum StatusLocacao {
	ATIVA,
	EM_ANDAMENTO,
	FINALIZADA;

	public boolean ativa() {
		return this == ATIVA;
	}

	public boolean emAndamento() {
		return this == EM_ANDAMENTO;
	}

	public boolean finalizada() {
		return this == FINALIZADA;
	}
}
