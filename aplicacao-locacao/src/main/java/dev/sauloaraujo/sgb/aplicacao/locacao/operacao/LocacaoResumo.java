package dev.sauloaraujo.sgb.aplicacao.locacao.operacao;

import java.math.BigDecimal;

/**
 * Interface de Resumo para Locação.
 * Representa uma projeção simplificada dos dados de Locação para a camada de aplicação.
 */
public interface LocacaoResumo {
	String getCodigo();
	
	String getReservaCodigo();
	
	String getVeiculoPlaca();
	
	String getVeiculoModelo();
	
	String getClienteNome();
	
	int getDiasPrevistos();
	
	BigDecimal getValorDiaria();
	
	String getStatus();
}
