package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object JPA para Checklist de Vistoria (embutido em Locação).
 */
@Embeddable
public class ChecklistVistoriaJpa {

	@Column(name = "quilometragem")
	private Integer quilometragem;

	@Column(name = "combustivel", length = 20)
	private String combustivel;

	@Column(name = "possui_avarias")
	private Boolean possuiAvarias;

	protected ChecklistVistoriaJpa() {
	}

	public Integer getQuilometragem() {
		return quilometragem;
	}

	public void setQuilometragem(Integer quilometragem) {
		this.quilometragem = quilometragem;
	}

	public String getCombustivel() {
		return combustivel;
	}

	public void setCombustivel(String combustivel) {
		this.combustivel = combustivel;
	}

	public Boolean getPossuiAvarias() {
		return possuiAvarias;
	}

	public void setPossuiAvarias(Boolean possuiAvarias) {
		this.possuiAvarias = possuiAvarias;
	}
}
