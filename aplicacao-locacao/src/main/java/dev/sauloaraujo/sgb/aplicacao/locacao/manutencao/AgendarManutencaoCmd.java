package dev.sauloaraujo.sgb.aplicacao.locacao.manutencao;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Command de aplicação para agendamento de manutenção de veículo.
 *
 * @param placa           placa do veículo
 * @param previsaoTermino data/hora prevista para término da manutenção
 * @param motivo          motivo da manutenção
 */
public record AgendarManutencaoCmd(
        String placa,
        LocalDateTime previsaoTermino,
        String motivo) {

    public AgendarManutencaoCmd {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("Placa do veículo é obrigatória");
        }
        Objects.requireNonNull(previsaoTermino, "Previsão de término é obrigatória");
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo da manutenção é obrigatório");
        }
    }
}

