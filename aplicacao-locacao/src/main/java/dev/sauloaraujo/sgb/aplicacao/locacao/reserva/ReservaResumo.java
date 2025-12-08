package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import java.math.BigDecimal;

/**
 * Interface de Resumo para Reserva.
 * Representa uma projeção simplificada dos dados de Reserva para a camada de aplicação.
 */
public interface ReservaResumo {
	String getCodigo();
	
	String getCategoria();
	
	String getCidadeRetirada();
	
	String getClienteNome();
	
	String getClienteCpf();
	
	BigDecimal getValorEstimado();
	
	String getStatus();
}
