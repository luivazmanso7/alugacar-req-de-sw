package dev.sauloaraujo.sgb.dominio.locacao.patio;

import java.util.Objects;

public class Patio {
	private final String codigo;
	private final String cidade;

	public Patio(String codigo, String cidade) {
		this.codigo = Objects.requireNonNull(codigo, "O código do pátio é obrigatório");
		this.cidade = Objects.requireNonNull(cidade, "A cidade do pátio é obrigatória");
	}

	public String getCodigo() {
		return codigo;
	}

	public String getCidade() {
		return cidade;
	}
}
