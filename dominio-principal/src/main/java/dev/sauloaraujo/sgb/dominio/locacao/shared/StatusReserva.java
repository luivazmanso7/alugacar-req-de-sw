package dev.sauloaraujo.sgb.dominio.locacao.shared;

public enum StatusReserva {
	ATIVA,
	CANCELADA,
	CONCLUIDA,
	EXPIRADA,
	EM_ANDAMENTO;

	public boolean ativa() {
		return this == ATIVA;
	}

	public boolean concluida() {
		return this == CONCLUIDA;
	}
}
