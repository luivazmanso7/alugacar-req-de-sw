package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object JPA para Pátio (embutido em Veículo).
 */
@Embeddable
public class PatioJpa {

	@Column(name = "patio_codigo", length = 50)
	private String codigo;

	@Column(name = "patio_localizacao", length = 100)
	private String localizacao;

	public PatioJpa() {
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
}
