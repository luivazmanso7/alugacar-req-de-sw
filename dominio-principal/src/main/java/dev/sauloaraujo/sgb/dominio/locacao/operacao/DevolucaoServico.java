package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.math.BigDecimal;
import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;

public class DevolucaoServico {
    private final LocacaoRepositorio locacaoRepositorio;
    private final VeiculoRepositorio veiculoRepositorio;

    public DevolucaoServico(LocacaoRepositorio locacaoRepositorio, VeiculoRepositorio veiculoRepositorio) {
        this.locacaoRepositorio = Objects
                .requireNonNull(locacaoRepositorio, "Repositorio de locações é obrigatório");
        this.veiculoRepositorio = Objects
                .requireNonNull(veiculoRepositorio, "Repositorio de veículos é obrigatório");
	}

	public Faturamento processar(ProcessarDevolucaoCommand command) {
		Objects.requireNonNull(command, "O comando é obrigatório");

		var locacao = locacaoRepositorio.buscarPorCodigoLocacao(command.getCodigoLocacao())
				.orElseThrow(() -> new IllegalArgumentException("Locação não encontrada"));

		var vistoria = new ChecklistVistoria(command.getQuilometragem(), command.getCombustivel(),
				command.isPossuiAvarias());
		locacao.registrarDevolucao(vistoria);

		var diasUtilizados = command.getDiasUtilizados() > 0 ? command.getDiasUtilizados() : locacao.getDiasPrevistos();
		var percentualMulta = command.getPercentualMultaAtraso() == null ? BigDecimal.ZERO
				: command.getPercentualMultaAtraso();

        var faturamento = locacao.finalizar(diasUtilizados, command.getDiasAtraso(), percentualMulta,
                command.getTaxaCombustivel(), command.isPossuiAvarias());

        locacaoRepositorio.salvar(locacao);
        veiculoRepositorio.salvar(locacao.getVeiculo());

        return faturamento;
    }
}
