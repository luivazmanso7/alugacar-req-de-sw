package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusLocacao;

public record ContratoLocacao(String codigoLocacao, String codigoReserva, String placaVeiculo, StatusLocacao status) {
}
