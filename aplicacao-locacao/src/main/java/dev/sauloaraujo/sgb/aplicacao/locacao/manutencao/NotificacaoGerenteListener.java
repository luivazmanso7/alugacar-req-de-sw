package dev.sauloaraujo.sgb.aplicacao.locacao.manutencao;

import java.util.Objects;
import java.util.logging.Logger;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import dev.sauloaraujo.sgb.dominio.locacao.evento.VeiculoFoiParaManutencaoEvent;

@Component
public class NotificacaoGerenteListener {

    private static final Logger LOGGER = Logger.getLogger(NotificacaoGerenteListener.class.getName());

    @EventListener
    public void aoAgendarManutencao(VeiculoFoiParaManutencaoEvent evento) {
        Objects.requireNonNull(evento, "Evento de veículo em manutenção é obrigatório");

        LOGGER.info(String.format(
                "Simulando envio de notificação ao gerente: veículo %s (categoria %s) entrou em manutenção. Motivo: %s. Início: %s. Previsão término: %s",
                evento.placa(),
                evento.categoria(),
                evento.motivo(),
                evento.dataInicio(),
                evento.previsaoTermino()));
    }
}

