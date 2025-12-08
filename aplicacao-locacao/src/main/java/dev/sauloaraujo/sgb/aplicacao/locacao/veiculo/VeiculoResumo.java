package dev.sauloaraujo.sgb.aplicacao.locacao.veiculo;

import java.math.BigDecimal;

/**
 * Interface de Resumo para Veículo.
 * Representa uma projeção simplificada dos dados de Veículo para a camada de aplicação.
 */
public interface VeiculoResumo {
	String getPlaca();
	
	String getModelo();
	
	String getCategoria();
	
	String getCidade();
	
	BigDecimal getDiaria();
	
	String getStatus();
}
