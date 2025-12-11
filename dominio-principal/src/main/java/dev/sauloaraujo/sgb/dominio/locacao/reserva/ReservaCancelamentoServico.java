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

    /**
     * Cancela uma reserva.
     * Valida regras de negócio:
     * - A reserva deve pertencer ao cliente informado (regra de negócio)
     * - Deve haver pelo menos 12 horas antes da data de retirada
     * - A reserva deve estar ativa
     * 
     * @param codigoReserva código da reserva a ser cancelada
     * @param cpfOuCnpjCliente CPF ou CNPJ do cliente que está cancelando
     * @param dataSolicitacao data/hora da solicitação de cancelamento
     * @return resultado do cancelamento com reserva e tarifa
     * @throws IllegalArgumentException se a reserva não for encontrada ou não pertencer ao cliente
     * @throws IllegalStateException se não houver 12 horas de antecedência ou reserva não estiver ativa
     */
    public ResultadoCancelamento cancelar(String codigoReserva, String cpfOuCnpjCliente, LocalDateTime dataSolicitacao) {
        Objects.requireNonNull(codigoReserva, "O código da reserva é obrigatório");
        Objects.requireNonNull(cpfOuCnpjCliente, "O CPF/CNPJ do cliente é obrigatório");
        Objects.requireNonNull(dataSolicitacao, "A data de solicitação é obrigatória");

        var reserva = reservaRepositorio.buscarPorCodigo(codigoReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));

        // REGRA DE NEGÓCIO: Uma reserva só pode ser cancelada pelo seu dono
        if (!reserva.getCliente().getCpfOuCnpj().equals(cpfOuCnpjCliente)) {
            throw new IllegalArgumentException("A reserva não pertence ao cliente informado");
        }

        // REGRA DE NEGÓCIO: Cancelamento só permitido com pelo menos 12 horas de antecedência
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
