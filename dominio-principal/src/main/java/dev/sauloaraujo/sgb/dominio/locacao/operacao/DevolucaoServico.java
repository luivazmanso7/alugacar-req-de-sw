package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.time.Duration;
import java.time.LocalDateTime;
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
	 * Este método agora é apenas uma orquestração simples seguindo Rich Domain Model:
	 * 1. Busca a locação no repositório
	 * 2. Cria o Value Object de vistoria
	 * 3. Calcula dias utilizados baseado na data real de devolução
	 * 4. DELEGA para a entidade Locacao (onde está a lógica de negócio)
	 * 5. Persiste as mudanças
	 * 6. Retorna o resultado
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

		// 3. Calcular dias utilizados
		int diasUtilizados;
		if (command.getDiasUtilizados() > 0) {
			// Se informado no comando, usar esse valor
			diasUtilizados = command.getDiasUtilizados();
		} else {
			// Calcular baseado na data real de devolução vs data de retirada
			var reserva = locacao.getReserva();
			var dataRetirada = reserva.getRetiradaInfo() != null 
				? reserva.getRetiradaInfo().dataHoraRetirada() 
				: reserva.getPeriodo().getRetirada();
			var dataDevolucao = LocalDateTime.now();
			
			var horas = Duration.between(dataRetirada, dataDevolucao).toHours();
			var diasCalculados = (int) (horas / 24);
			if (horas % 24 != 0) {
				diasCalculados++;
			}
			
			// Garantir pelo menos 1 dia
			diasUtilizados = Math.max(1, diasCalculados);
		}

		// 4. DELEGAR PARA A ENTIDADE (Rich Domain Model!)
		// A entidade contém toda a lógica de cálculo e regras de negócio
		var faturamento = locacao.realizarDevolucao(vistoria, diasUtilizados);

		// 5. Persistir Estado Atualizado
		locacaoRepositorio.salvar(locacao);
		veiculoRepositorio.salvar(locacao.getVeiculo());

		// 6. Retornar Resultado
		return faturamento;
	}
}
