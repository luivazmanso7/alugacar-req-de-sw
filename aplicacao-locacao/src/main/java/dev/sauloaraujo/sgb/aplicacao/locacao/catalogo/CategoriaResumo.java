package dev.sauloaraujo.sgb.aplicacao.locacao.catalogo;

import java.math.BigDecimal;

/**
 * DTO de resumo de categoria para a camada de apresentação.
 */
public record CategoriaResumo(
        String codigo,
        String nome,
        String descricao,
        BigDecimal diaria,
        int quantidadeDisponivel
) {}

