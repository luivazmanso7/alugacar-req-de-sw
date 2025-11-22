package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object JPA para Pátio (embutido em Veículo).
 * Representa o local físico onde o veículo está estacionado.
 */
@Embeddable
class PatioJpa {

	@Column(name = "patio_codigo", length = 50)
	String codigo;

	@Column(name = "patio_cidade", length = 100)
	String cidade;
}
