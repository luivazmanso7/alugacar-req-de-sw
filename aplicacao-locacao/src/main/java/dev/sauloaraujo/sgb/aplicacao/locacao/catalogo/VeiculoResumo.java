package dev.sauloaraujo.sgb.aplicacao.locacao.catalogo;

import java.math.BigDecimal;

/**
 * DTO de resumo de veículo para a camada de apresentação.
 */
public record VeiculoResumo(
        String placa,
        String modelo,
        String categoria,
        String cidade,
        BigDecimal diaria,
        String status
) {}

