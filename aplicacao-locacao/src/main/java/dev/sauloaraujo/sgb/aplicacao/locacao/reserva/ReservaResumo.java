package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resumo de reserva para a camada de aplicação / apresentação.
 */
public record ReservaResumo(
        String codigo,
        String categoria,
        String cidadeRetirada,
        LocalDateTime dataRetirada,
        LocalDateTime dataDevolucao,
        BigDecimal valorEstimado,
        String status,
        String clienteNome,
        String clienteDocumento,
        String placaVeiculo) {
}

