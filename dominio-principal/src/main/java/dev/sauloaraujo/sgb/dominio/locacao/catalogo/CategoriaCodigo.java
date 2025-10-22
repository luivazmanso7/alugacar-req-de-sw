package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

public enum CategoriaCodigo {
	ECONOMICO,
	INTERMEDIARIO,
	EXECUTIVO,
	PREMIUM,
	SUV;

	public static CategoriaCodigo fromTexto(String valor) {
		if (valor == null) {
			throw new IllegalArgumentException("O código da categoria é obrigatório");
		}

		return CategoriaCodigo.valueOf(valor.trim().toUpperCase());
	}
}
