package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import java.math.BigDecimal;

/**
 * DTO de resposta com informações do cancelamento de reserva.
 */
public record CancelarReservaResponse(
        String codigoReserva,
        String status,
        BigDecimal tarifaCancelamento
) {}

