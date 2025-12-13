package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RetiradaInfoJpa {
	
	@Column(name = "retirada_placa_veiculo", length = 10)
	private String placaVeiculo;
	
	@Column(name = "retirada_cnh_condutor", length = 11)
	private String cnhCondutor;
	
	@Column(name = "retirada_data_hora")
	private LocalDateTime dataHoraRetirada;
	
	@Column(name = "retirada_quilometragem_saida")
	private Long quilometragemSaida;
	
	@Column(name = "retirada_nivel_tanque", length = 20)
	private String nivelTanqueSaida;
	
	@Column(name = "retirada_observacoes", length = 500)
	private String observacoes;
	
	public RetiradaInfoJpa() {
	}
	
	public String getPlacaVeiculo() {
		return placaVeiculo;
	}
	
	public void setPlacaVeiculo(String placaVeiculo) {
		this.placaVeiculo = placaVeiculo;
	}
	
	public String getCnhCondutor() {
		return cnhCondutor;
	}
	
	public void setCnhCondutor(String cnhCondutor) {
		this.cnhCondutor = cnhCondutor;
	}
	
	public LocalDateTime getDataHoraRetirada() {
		return dataHoraRetirada;
	}
	
	public void setDataHoraRetirada(LocalDateTime dataHoraRetirada) {
		this.dataHoraRetirada = dataHoraRetirada;
	}
	
	public Long getQuilometragemSaida() {
		return quilometragemSaida;
	}
	
	public void setQuilometragemSaida(Long quilometragemSaida) {
		this.quilometragemSaida = quilometragemSaida;
	}
	
	public String getNivelTanqueSaida() {
		return nivelTanqueSaida;
	}
	
	public void setNivelTanqueSaida(String nivelTanqueSaida) {
		this.nivelTanqueSaida = nivelTanqueSaida;
	}
	
	public String getObservacoes() {
		return observacoes;
	}
	
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
}

