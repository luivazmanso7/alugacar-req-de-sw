package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import java.time.LocalDateTime;

public record ConfirmarRetiradaCmd(
	String codigoReserva,
	String placaVeiculo,
	String cnhCondutor,
	LocalDateTime dataHoraRetirada,
	long quilometragemSaida,
	String nivelTanqueSaida,
	String observacoes
) {
}

