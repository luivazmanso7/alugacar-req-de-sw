package dev.sauloaraujo.sgb.dominio.locacao.evento;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Evento de domínio disparado quando um veículo é colocado em manutenção.
 * <p>
 * Implementado como {@code record} imutável para garantir segurança de
 * thread e facilitar o transporte entre camadas.
 * </p>
 *
 * @param placa           placa do veículo
 * @param categoria       categoria do veículo (ex.: SUV, ECONOMICO)
 * @param motivo          motivo da manutenção agendada
 * @param dataInicio      data e hora em que a manutenção foi registrada
 * @param previsaoTermino previsão de término da manutenção
 */
public record VeiculoFoiParaManutencaoEvent(
        String placa,
        String categoria,
        String motivo,
        LocalDateTime dataInicio,
        LocalDateTime previsaoTermino) {

    public VeiculoFoiParaManutencaoEvent {
        notBlank(placa, "Placa do veículo não pode ser vazia");
        notBlank(categoria, "Categoria do veículo não pode ser vazia");
        notBlank(motivo, "Motivo da manutenção não pode ser vazio");
        Objects.requireNonNull(dataInicio, "Data de início da manutenção é obrigatória");
        Objects.requireNonNull(previsaoTermino, "Previsão de término da manutenção é obrigatória");
    }

    private static void notBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}

