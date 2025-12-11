package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Command de aplicação para cancelamento de reserva.
 * 
 * @param codigoReserva código da reserva a ser cancelada
 * @param dataSolicitacao data/hora da solicitação de cancelamento
 * @param cpfOuCnpjCliente CPF ou CNPJ do cliente que está cancelando (para validação de autorização)
 */
public record CancelarReservaCmd(
        String codigoReserva,
        LocalDateTime dataSolicitacao,
        String cpfOuCnpjCliente) {

    public CancelarReservaCmd {
        Objects.requireNonNull(codigoReserva, "Código da reserva é obrigatório");
        if (codigoReserva.isBlank()) {
            throw new IllegalArgumentException("Código da reserva não pode estar vazio");
        }
        Objects.requireNonNull(dataSolicitacao, "Data de solicitação é obrigatória");
        Objects.requireNonNull(cpfOuCnpjCliente, "CPF/CNPJ do cliente é obrigatório");
        if (cpfOuCnpjCliente.isBlank()) {
            throw new IllegalArgumentException("CPF/CNPJ do cliente não pode estar vazio");
        }
    }
}

