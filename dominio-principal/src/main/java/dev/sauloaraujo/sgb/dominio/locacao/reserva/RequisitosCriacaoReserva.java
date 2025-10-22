package dev.sauloaraujo.sgb.dominio.locacao.reserva;

public record RequisitosCriacaoReserva(boolean exigeDadosPessoais, boolean exigeCategoria,
		boolean exigePeriodoLocacao, boolean exigeCidadeRetirada) {
}
