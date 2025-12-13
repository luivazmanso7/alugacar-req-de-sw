package dev.sauloaraujo.sgb.aplicacao.locacao.manutencao;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.manutencao.ManutencaoServico;

@Service
public class ManutencaoServicoAplicacao {

    private final ManutencaoServico manutencaoServico;
    private final ApplicationEventPublisher eventPublisher;
    private final VeiculoRepositorio veiculoRepositorio;

    public ManutencaoServicoAplicacao(ManutencaoServico manutencaoServico,
                                      ApplicationEventPublisher eventPublisher,
                                      VeiculoRepositorio veiculoRepositorio) {
        this.manutencaoServico = Objects.requireNonNull(manutencaoServico,
                "Serviço de manutenção de domínio é obrigatório");
        this.eventPublisher = Objects.requireNonNull(eventPublisher,
                "Publicador de eventos da aplicação é obrigatório");
        this.veiculoRepositorio = notNull(veiculoRepositorio,
                "Repositório de veículos é obrigatório");
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

    @Transactional(readOnly = true)
    public List<VeiculoManutencaoResumo> listarQuePrecisamManutencao() {
        var veiculos = veiculoRepositorio.buscarQuePrecisamManutencao();
        return veiculos.stream()
                .map(this::toResumoManutencao)
                .collect(Collectors.toList());
    }

    private VeiculoManutencaoResumo toResumoManutencao(Veiculo veiculo) {
        return new VeiculoManutencaoResumo(
                veiculo.getPlaca(),
                veiculo.getModelo(),
                veiculo.getCategoria().name(),
                veiculo.getCidade(),
                veiculo.getDiaria(),
                veiculo.getStatus().name(),
                veiculo.getManutencaoPrevista(),
                veiculo.getManutencaoNota()
        );
    }
}

