package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaCancelamentoServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaReplanejamentoServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaServico;

/**
 * Serviço de aplicação para operações de reserva.
 * Coordena casos de uso relacionados a reservas.
 */
@Service
public class ReservaServicoAplicacao {

    private final ReservaServico reservaServico;
    private final ReservaCancelamentoServico cancelamentoServico;
    private final ReservaReplanejamentoServico replanejamentoServico;

    public ReservaServicoAplicacao(
            ReservaServico reservaServico,
            ReservaCancelamentoServico cancelamentoServico,
            ReservaReplanejamentoServico replanejamentoServico) {
        this.reservaServico = notNull(reservaServico, "Serviço de reserva de domínio é obrigatório");
        this.cancelamentoServico = notNull(cancelamentoServico, "Serviço de cancelamento é obrigatório");
        this.replanejamentoServico = notNull(replanejamentoServico, "Serviço de replanejamento é obrigatório");
    }

    /**
     * Cria uma nova reserva.
     * Delega a lógica de negócio para o domínio e garante a transação.
     * 
     * @param comando comando contendo os dados da reserva
     * @return resumo da reserva criada
     */
    @Transactional
    public ReservaResumo criar(CriarReservaCmd comando) {
        Objects.requireNonNull(comando, "Comando de criação é obrigatório");

        // Gera código único para a reserva
        String codigo = gerarCodigoReserva();

        // Delega para o serviço de domínio que executa toda a lógica de negócio
        Reserva reserva = reservaServico.criarReserva(
                codigo,
                comando.categoriaCodigo(),
                comando.cidadeRetirada(),
                comando.periodo(),
                comando.cliente(),
                comando.placaVeiculo()
        );

        // Converte entidade de domínio para DTO de resumo
        return toResumo(reserva);
    }

    /**
     * Gera um código único para a reserva.
     */
    private String gerarCodigoReserva() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Cancela uma reserva.
     * Delega TODAS as regras de negócio para o domínio.
     * A camada de aplicação apenas coordena e converte DTOs.
     * 
     * @param comando comando contendo os dados do cancelamento
     * @return resposta com informações do cancelamento incluindo tarifa
     * @throws IllegalArgumentException se a reserva não for encontrada ou não pertencer ao cliente
     * @throws IllegalStateException se não houver 12 horas de antecedência ou reserva não estiver ativa
     */
    @Transactional
    public CancelarReservaResponse cancelar(CancelarReservaCmd comando) {
        Objects.requireNonNull(comando, "Comando de cancelamento é obrigatório");

        // Delega TODAS as regras de negócio para o domínio:
        // - Validação de propriedade (reserva pertence ao cliente)
        // - Validação de 12 horas de antecedência
        // - Validação de status (reserva deve estar ativa)
        // - Cálculo de tarifa
        var resultado = cancelamentoServico.cancelar(
                comando.codigoReserva(),
                comando.cpfOuCnpjCliente(),
                comando.dataSolicitacao()
        );

        // Converte resultado de domínio para DTO de resposta
        return new CancelarReservaResponse(
                resultado.reserva().getCodigo(),
                resultado.reserva().getStatus().name(),
                resultado.tarifa()
        );
    }

    /**
     * Altera o período de uma reserva (replanejamento).
     * Delega a lógica de negócio para o domínio e garante a transação.
     * 
     * @param comando comando contendo os dados da alteração
     * @return resumo da reserva alterada
     */
    @Transactional
    public ReservaResumo alterar(AlterarReservaCmd comando) {
        Objects.requireNonNull(comando, "Comando de alteração é obrigatório");

        // Delega para o serviço de domínio que executa toda a lógica de negócio
        Reserva reserva = replanejamentoServico.replanejar(
                comando.codigoReserva(),
                comando.novoPeriodo()
        );

        // Converte entidade de domínio para DTO de resumo
        return toResumo(reserva);
    }

    /**
     * Converte entidade de domínio para DTO de resumo.
     */
    private ReservaResumo toResumo(Reserva reserva) {
        return new ReservaResumo(
                reserva.getCodigo(),
                reserva.getCategoria().name(),
                reserva.getCidadeRetirada(),
                reserva.getPeriodo().getRetirada(),
                reserva.getPeriodo().getDevolucao(),
                reserva.getValorEstimado(),
                reserva.getStatus().name(),
                reserva.getCliente().getNome(),
                reserva.getCliente().getCpfOuCnpj()
        );
    }
}

