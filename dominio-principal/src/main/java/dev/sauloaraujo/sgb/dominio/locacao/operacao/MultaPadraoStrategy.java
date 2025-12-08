package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.math.BigDecimal;

/**
 * Estratégia padrão para cálculo de multa por atraso.
 * 
 * <p>
 * Aplica o percentual de multa sobre o valor total do atraso.
 * </p>
 * 
 * <p>
 * <strong>Padrão de Projeto:</strong> Strategy (GoF) - Implementação Concreta
 * </p>
 * 
 * @since 2.0
 * @version 2.0
 */
public class MultaPadraoStrategy implements CalculoMultaStrategy {

	@Override
	public BigDecimal calcular(BigDecimal valorAtraso, BigDecimal percentualMultaAtraso) {
		if (valorAtraso == null || valorAtraso.signum() <= 0) {
			return BigDecimal.ZERO;
		}
		if (percentualMultaAtraso == null || percentualMultaAtraso.signum() <= 0) {
			return BigDecimal.ZERO;
		}
		return valorAtraso.multiply(percentualMultaAtraso);
	}
}
