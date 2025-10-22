package dev.sauloaraujo.sgb.dominio.locacao.shared;

public enum StatusLocacao {
	ATIVA,
	FINALIZADA;

	public boolean ativa() {
		return this == ATIVA;
	}

	public boolean finalizada() {
		return this == FINALIZADA;
	}
}
