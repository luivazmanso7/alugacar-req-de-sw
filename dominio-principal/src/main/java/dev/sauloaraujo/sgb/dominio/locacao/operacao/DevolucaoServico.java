package dev.sauloaraujo.sgb.dominio.locacao.operacao;

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

	/**
	 * Processa a devolução de uma locação.
	 * 
	 * <p>Este Domain Service orquestra a devolução seguindo Rich Domain Model:
	 * 1. Busca a locação no repositório
	 * 2. Cria o Value Object de vistoria
	 * 3. DELEGA para a entidade Locacao (onde está TODA a lógica de negócio)
	 *    - A entidade calcula dias utilizados e dias de atraso internamente
	 *    - A entidade calcula todos os valores (diárias, atraso, multa, taxas)
	 *    - A entidade atualiza seu próprio estado e o estado do veículo
	 * 4. Persiste as mudanças
	 * 5. Retorna o resultado
	 * </p>
	 * 
	 * @param command Comando com dados da devolução
	 * @return Faturamento calculado
	 */
	public Faturamento processar(ProcessarDevolucaoCommand command) {
		Objects.requireNonNull(command, "O comando é obrigatório");

		// 1. Buscar Locação
		var locacao = locacaoRepositorio.buscarPorCodigoLocacao(command.getCodigoLocacao())
				.orElseThrow(() -> new IllegalArgumentException(
					"Locação não encontrada: " + command.getCodigoLocacao()
				));

		// 2. Criar Value Object de Vistoria
		var vistoria = new ChecklistVistoria(
			command.getQuilometragem(), 
			command.getCombustivel(),
			command.isPossuiAvarias()
		);

		var faturamento = locacao.realizarDevolucao(
			vistoria, 
			command.getDataDevolucao(), 
			command.getPercentualMultaAtraso()
		);

		// 4. Persistir Estado Atualizado
		locacaoRepositorio.salvar(locacao);
		veiculoRepositorio.salvar(locacao.getVeiculo());

		// 5. Retornar Resultado
		return faturamento;
	}
}
