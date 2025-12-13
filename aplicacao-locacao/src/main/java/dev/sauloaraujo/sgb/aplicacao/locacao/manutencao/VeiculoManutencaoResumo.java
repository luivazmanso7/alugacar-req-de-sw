package dev.sauloaraujo.sgb.aplicacao.locacao.manutencao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resumo de veículo que precisa de manutenção.
 * Usado para listar veículos que foram enviados para manutenção devido a avarias,
 * mas ainda não tiveram a manutenção agendada (sem data prevista).
 */
public record VeiculoManutencaoResumo(
        String placa,
        String modelo,
        String categoria,
        String cidade,
        BigDecimal diaria,
        String status,
        LocalDateTime manutencaoPrevista,
        String manutencaoNota
) {}

