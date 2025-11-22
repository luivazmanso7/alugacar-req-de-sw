package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object JPA para Checklist de Vistoria.
 * Embutido em Locação (vistoria de retirada e devolução).
 */
@Embeddable
class ChecklistVistoriaJpa {

	@Column(name = "vistoria_quilometragem")
	Integer quilometragem;

	@Column(name = "vistoria_combustivel", length = 20)
	String combustivel;

	@Column(name = "vistoria_possui_avarias")
	Boolean possuiAvarias;
}
