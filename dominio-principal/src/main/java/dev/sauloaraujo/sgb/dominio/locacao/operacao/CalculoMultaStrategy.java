package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.math.BigDecimal;

/**
 * Interface Strategy para cálculo de multa por atraso na devolução.
 * 
 * <p>
 * Este padrão permite que diferentes políticas de multa sejam aplicadas de forma
 * flexível, sem modificar a lógica da entidade {@link Locacao}.
 * </p>
 * 
 * <p>
 * <strong>Padrão de Projeto:</strong> Strategy (GoF)
 * </p>
 * 
 * @since 2.0
 * @version 2.0
 */
public interface CalculoMultaStrategy {

	/**
	 * Calcula o valor da multa com base no valor do atraso e percentual.
	 * 
	 * @param valorAtraso          valor total do atraso (dias * diária)
	 * @param percentualMultaAtraso percentual da multa a ser aplicado
	 * @return valor da multa calculado, nunca {@code null}
	 */
	BigDecimal calcular(BigDecimal valorAtraso, BigDecimal percentualMultaAtraso);
}
