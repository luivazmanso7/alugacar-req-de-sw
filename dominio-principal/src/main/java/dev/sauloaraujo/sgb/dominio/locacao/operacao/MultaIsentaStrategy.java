package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.math.BigDecimal;

/**
 * Estratégia de isenção de multa por atraso.
 * 
 * <p>
 * Utilizada para clientes VIP, campanhas promocionais ou situações especiais onde a
 * multa não deve ser aplicada.
 * </p>
 * 
 * <p>
 * <strong>Padrão de Projeto:</strong> Strategy (GoF) - Implementação Concreta
 * </p>
 * 
 * @since 2.0
 * @version 2.0
 */
public class MultaIsentaStrategy implements CalculoMultaStrategy {

	@Override
	public BigDecimal calcular(BigDecimal valorAtraso, BigDecimal percentualMultaAtraso) {
		return BigDecimal.ZERO;
	}
}
