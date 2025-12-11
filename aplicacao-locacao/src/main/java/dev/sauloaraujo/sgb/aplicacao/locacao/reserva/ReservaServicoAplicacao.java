package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaServico;

/**
 * Serviço de aplicação para operações de reserva.
 * Coordena casos de uso relacionados a reservas.
 */
@Service
public class ReservaServicoAplicacao {

    private final ReservaServico reservaServico;

    public ReservaServicoAplicacao(ReservaServico reservaServico) {
        this.reservaServico = notNull(reservaServico, "Serviço de reserva de domínio é obrigatório");
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
                comando.cliente()
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

