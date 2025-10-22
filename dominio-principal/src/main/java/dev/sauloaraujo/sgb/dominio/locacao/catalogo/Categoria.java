package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Categoria {
	private final CategoriaCodigo codigo;
	private final String nome;
	private final String descricao;
	private final BigDecimal diaria;
	private final List<String> modelosExemplo;
	private int quantidadeDisponivel;

	public Categoria(CategoriaCodigo codigo, String nome, String descricao, BigDecimal diaria,
			List<String> modelosExemplo, int quantidadeDisponivel) {
		this.codigo = Objects.requireNonNull(codigo, "O código da categoria é obrigatório");
		this.nome = Objects.requireNonNull(nome, "O nome da categoria é obrigatório");
		this.descricao = Objects.requireNonNull(descricao, "A descrição da categoria é obrigatória");
		this.diaria = Objects.requireNonNull(diaria, "O valor da diária é obrigatório");
		if (diaria.signum() <= 0) {
			throw new IllegalArgumentException("O valor da diária deve ser positivo");
		}

		this.modelosExemplo = List.copyOf(Objects.requireNonNull(modelosExemplo, "Os modelos são obrigatórios"));
		this.quantidadeDisponivel = Math.max(quantidadeDisponivel, 0);
	}

	public CategoriaCodigo getCodigo() {
		return codigo;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public BigDecimal getDiaria() {
		return diaria;
	}

	public List<String> getModelosExemplo() {
		return modelosExemplo;
	}

	public int getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}

	public void reservarDisponibilidade() {
		if (quantidadeDisponivel <= 0) {
			throw new IllegalStateException("Não há veículos disponíveis na categoria " + codigo);
		}
		quantidadeDisponivel--;
	}

	public void liberarDisponibilidade() {
		quantidadeDisponivel++;
	}
}
