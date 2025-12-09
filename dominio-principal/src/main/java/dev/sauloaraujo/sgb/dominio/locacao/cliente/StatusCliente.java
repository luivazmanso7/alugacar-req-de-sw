package dev.sauloaraujo.sgb.dominio.locacao.cliente;

/**
 * Status possíveis de um cliente no sistema.
 */
public enum StatusCliente {
	/**
	 * Cliente ativo, pode fazer reservas e locações.
	 */
	ATIVO,
	
	/**
	 * Cliente temporariamente bloqueado (inadimplência, multas pendentes, etc).
	 */
	BLOQUEADO,
	
	/**
	 * Cliente inativo, não pode realizar operações.
	 */
	INATIVO
}
