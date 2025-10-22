package dev.sauloaraujo.sgb.dominio.locacao.shared;

public enum StatusVeiculo {
	DISPONIVEL,
	RESERVADO,
	LOCADO,
	EM_MANUTENCAO,
	VENDIDO;

	public boolean disponivel() {
		return this == DISPONIVEL;
	}

	public boolean locado() {
		return this == LOCADO;
	}
}
