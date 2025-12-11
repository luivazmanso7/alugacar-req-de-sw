package dev.sauloaraujo.sgb.aplicacao.locacao.operacao;

/**
 * DTO de resposta com informações do contrato de locação gerado.
 */
public record ContratoResponse(
        String codigoLocacao,
        String codigoReserva,
        String placaVeiculo,
        String status
) {}

