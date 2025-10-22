package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.math.BigDecimal;

public record Faturamento(BigDecimal total, BigDecimal diarias, BigDecimal valorAtraso, BigDecimal multaAtraso,
		BigDecimal taxasAdicionais) {
}
