package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;

/**
 * Command de aplicação para alteração de período de reserva.
 * 
 * @param codigoReserva código da reserva a ser alterada
 * @param novoPeriodo novo período de locação (retirada e devolução)
 */
public record AlterarReservaCmd(
        String codigoReserva,
        PeriodoLocacao novoPeriodo) {

    public AlterarReservaCmd {
        Objects.requireNonNull(codigoReserva, "Código da reserva é obrigatório");
        if (codigoReserva.isBlank()) {
            throw new IllegalArgumentException("Código da reserva não pode estar vazio");
        }
        Objects.requireNonNull(novoPeriodo, "Novo período é obrigatório");
    }
}

