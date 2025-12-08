package dev.sauloaraujo.sgb.aplicacao.locacao.cliente;

/**
 * Interface de Resumo para Cliente.
 * Representa uma projeção simplificada dos dados de Cliente para a camada de aplicação.
 */
public interface ClienteResumo {
	String getNome();
	
	String getCpfOuCnpj();
	
	String getCnh();
	
	String getEmail();
}
