package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import java.time.LocalDateTime;
import java.util.Objects;

public record RetiradaInfo(
	String placaVeiculo,
	String cnhCondutor,
	LocalDateTime dataHoraRetirada,
	long quilometragemSaida,
	String nivelTanqueSaida,
	String observacoes
) {
	public RetiradaInfo {
		Objects.requireNonNull(placaVeiculo, "A placa do veículo é obrigatória");
		if (placaVeiculo.isBlank()) {
			throw new IllegalArgumentException("A placa do veículo não pode estar vazia");
		}
		
		Objects.requireNonNull(cnhCondutor, "A CNH do condutor é obrigatória");
		if (cnhCondutor.isBlank()) {
			throw new IllegalArgumentException("A CNH do condutor não pode estar vazia");
		}
		
		Objects.requireNonNull(dataHoraRetirada, "A data e hora da retirada são obrigatórias");
		
		if (quilometragemSaida < 0) {
			throw new IllegalArgumentException("A quilometragem de saída não pode ser negativa");
		}
		
		Objects.requireNonNull(nivelTanqueSaida, "O nível do tanque é obrigatório");
		if (nivelTanqueSaida.isBlank()) {
			throw new IllegalArgumentException("O nível do tanque não pode estar vazio");
		}
		
		observacoes = observacoes != null ? observacoes : "";
	}
}

