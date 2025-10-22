package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReservaCancelamentoServico {
    private static final long LIMITE_HORAS_CANCELAMENTO = 12;

    private final ReservaRepositorio reservaRepositorio;

    public ReservaCancelamentoServico(ReservaRepositorio reservaRepositorio) {
        this.reservaRepositorio = Objects.requireNonNull(reservaRepositorio,
                "Repositorio de reservas é obrigatório");
    }

    public ResultadoCancelamento cancelar(String codigoReserva, LocalDateTime dataSolicitacao) {
        Objects.requireNonNull(codigoReserva, "O código da reserva é obrigatório");
        Objects.requireNonNull(dataSolicitacao, "A data de solicitação é obrigatória");

        var reserva = reservaRepositorio.buscarPorCodigo(codigoReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));

        var horasAteRetirada = Duration.between(dataSolicitacao, reserva.getPeriodo().getRetirada()).toHours();
        if (horasAteRetirada < LIMITE_HORAS_CANCELAMENTO) {
            throw new IllegalStateException("Cancelamento não permitido nas últimas 12 horas");
        }

        var tarifa = calcularTarifa(reserva, dataSolicitacao);
        reserva.cancelarComTarifa(tarifa);
        reservaRepositorio.salvar(reserva);
        return new ResultadoCancelamento(reserva, tarifa);
    }

    private BigDecimal calcularTarifa(Reserva reserva, LocalDateTime dataSolicitacao) {
        return BigDecimal.ZERO;
    }

    public record ResultadoCancelamento(Reserva reserva, BigDecimal tarifa) {
    }
}
