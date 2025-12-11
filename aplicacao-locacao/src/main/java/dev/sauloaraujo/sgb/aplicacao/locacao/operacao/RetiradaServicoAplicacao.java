package dev.sauloaraujo.sgb.aplicacao.locacao.operacao;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.operacao.ContratoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ProcessarRetiradaCommand;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.RetiradaServico;

/**
 * Serviço de aplicação para operações de retirada de veículos.
 * Coordena a confirmação de retirada e criação de contrato de locação.
 */
@Service
public class RetiradaServicoAplicacao {

    private final RetiradaServico retiradaServico;

    public RetiradaServicoAplicacao(RetiradaServico retiradaServico) {
        this.retiradaServico = notNull(retiradaServico, "Serviço de retirada de domínio é obrigatório");
    }

    /**
     * Confirma a retirada de um veículo e gera o contrato de locação.
     * Delega a lógica de negócio para o domínio e garante a transação.
     * 
     * @param comando comando contendo os dados da retirada
     * @return resumo do contrato gerado
     */
    @Transactional
    public ContratoResponse confirmar(ConfirmarRetiradaCmd comando) {
        Objects.requireNonNull(comando, "Comando de confirmação é obrigatório");

        // Gera código único para a locação
        String codigoLocacao = gerarCodigoLocacao();

        // Converte Command de aplicação para Command de domínio
        ProcessarRetiradaCommand commandDominio = ProcessarRetiradaCommand.builder()
                .codigoReserva(comando.codigoReserva())
                .codigoLocacao(codigoLocacao)
                .placaVeiculo(comando.placaVeiculo())
                .documentosValidos(comando.documentosValidos())
                .quilometragem(comando.quilometragem())
                .combustivel(comando.combustivel())
                .build();

        // Delega para o serviço de domínio que executa toda a lógica de negócio
        ContratoLocacao contrato = retiradaServico.processar(commandDominio);

        // Converte contrato de domínio para DTO de resposta
        return toResponse(contrato);
    }

    /**
     * Gera um código único para a locação.
     */
    private String gerarCodigoLocacao() {
        return "LOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Converte contrato de domínio para DTO de resposta.
     */
    private ContratoResponse toResponse(ContratoLocacao contrato) {
        return new ContratoResponse(
                contrato.codigoLocacao(),
                contrato.codigoReserva(),
                contrato.placaVeiculo(),
                contrato.status().name()
        );
    }
}

