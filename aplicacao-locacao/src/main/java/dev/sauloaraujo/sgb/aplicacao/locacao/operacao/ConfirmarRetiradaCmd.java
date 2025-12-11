package dev.sauloaraujo.sgb.aplicacao.locacao.operacao;

import java.util.Objects;

/**
 * Command de aplicação para confirmação de retirada de veículo.
 * 
 * @param codigoReserva código da reserva a ser confirmada
 * @param placaVeiculo placa do veículo a ser locado
 * @param documentosValidos indica se os documentos do cliente estão válidos
 * @param quilometragem quilometragem atual do veículo
 * @param combustivel nível de combustível (ex: "CHEIO", "MEIO", "VAZIO")
 */
public record ConfirmarRetiradaCmd(
        String codigoReserva,
        String placaVeiculo,
        boolean documentosValidos,
        int quilometragem,
        String combustivel) {

    public ConfirmarRetiradaCmd {
        Objects.requireNonNull(codigoReserva, "Código da reserva é obrigatório");
        if (codigoReserva.isBlank()) {
            throw new IllegalArgumentException("Código da reserva não pode estar vazio");
        }
        Objects.requireNonNull(placaVeiculo, "Placa do veículo é obrigatória");
        if (placaVeiculo.isBlank()) {
            throw new IllegalArgumentException("Placa do veículo não pode estar vazia");
        }
        Objects.requireNonNull(combustivel, "Nível de combustível é obrigatório");
        if (combustivel.isBlank()) {
            throw new IllegalArgumentException("Nível de combustível não pode estar vazio");
        }
        if (quilometragem < 0) {
            throw new IllegalArgumentException("Quilometragem não pode ser negativa");
        }
    }
}

