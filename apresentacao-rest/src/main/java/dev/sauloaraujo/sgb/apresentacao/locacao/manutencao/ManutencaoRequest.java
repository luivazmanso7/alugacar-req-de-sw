package dev.sauloaraujo.sgb.apresentacao.locacao.manutencao;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ManutencaoRequest(
        @NotNull
        @Future(message = "A previsão de término deve ser uma data futura")
        LocalDateTime previsaoTermino,

        @NotBlank(message = "O motivo da manutenção é obrigatório")
        String motivo) {
}

