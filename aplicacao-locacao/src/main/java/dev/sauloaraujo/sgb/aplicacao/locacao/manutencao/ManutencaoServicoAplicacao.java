package dev.sauloaraujo.sgb.aplicacao.locacao.manutencao;

import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.manutencao.ManutencaoServico;

@Service
public class ManutencaoServicoAplicacao {

    private final ManutencaoServico manutencaoServico;
    private final ApplicationEventPublisher eventPublisher;

    public ManutencaoServicoAplicacao(ManutencaoServico manutencaoServico,
                                      ApplicationEventPublisher eventPublisher) {
        this.manutencaoServico = Objects.requireNonNull(manutencaoServico,
                "Serviço de manutenção de domínio é obrigatório");
        this.eventPublisher = Objects.requireNonNull(eventPublisher,
                "Publicador de eventos da aplicação é obrigatório");
    }

    @Transactional
    public void agendar(AgendarManutencaoCmd comando) {
        Objects.requireNonNull(comando, "Comando de agendamento é obrigatório");

        var evento = manutencaoServico.agendar(
                comando.placa(),
                comando.previsaoTermino(),
                comando.motivo());

        eventPublisher.publishEvent(evento);
    }
}

