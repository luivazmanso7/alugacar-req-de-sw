package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.math.BigDecimal;
import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusLocacao;

public class Locacao {
	private final String codigo;
	private final Reserva reserva;
	private final Veiculo veiculo;
	private final int diasPrevistos;
	private final BigDecimal valorDiaria;
	private final CalculoMultaStrategy estrategiaMulta;
	private StatusLocacao status;
	private ChecklistVistoria vistoriaRetirada;
	private ChecklistVistoria vistoriaDevolucao;

	public Locacao(String codigo, Reserva reserva, Veiculo veiculo, int diasPrevistos, BigDecimal valorDiaria,
			ChecklistVistoria vistoriaRetirada, CalculoMultaStrategy estrategiaMulta) {
		this.codigo = Objects.requireNonNull(codigo, "O código da locação é obrigatório");
		this.reserva = Objects.requireNonNull(reserva, "A reserva é obrigatória");
		this.veiculo = Objects.requireNonNull(veiculo, "O veículo é obrigatório");
		if (diasPrevistos <= 0) {
			throw new IllegalArgumentException("Dias previstos devem ser positivos");
		}
		this.diasPrevistos = diasPrevistos;
		this.valorDiaria = Objects.requireNonNull(valorDiaria, "O valor da diária é obrigatório");
		this.vistoriaRetirada = Objects.requireNonNull(vistoriaRetirada, "A vistoria de retirada é obrigatória");
		this.estrategiaMulta = Objects.requireNonNull(estrategiaMulta, "A estratégia de cálculo de multa é obrigatória");
		this.status = StatusLocacao.ATIVA;
	}

	public Locacao(String codigo, Reserva reserva, Veiculo veiculo, int diasPrevistos, BigDecimal valorDiaria,
			ChecklistVistoria vistoriaRetirada, CalculoMultaStrategy estrategiaMulta, StatusLocacao statusInicial) {
		this.codigo = Objects.requireNonNull(codigo, "O código da locação é obrigatório");
		this.reserva = Objects.requireNonNull(reserva, "A reserva é obrigatória");
		this.veiculo = Objects.requireNonNull(veiculo, "O veículo é obrigatório");
		if (diasPrevistos <= 0) {
			throw new IllegalArgumentException("Dias previstos devem ser positivos");
		}
		this.diasPrevistos = diasPrevistos;
		this.valorDiaria = Objects.requireNonNull(valorDiaria, "O valor da diária é obrigatório");
		this.vistoriaRetirada = Objects.requireNonNull(vistoriaRetirada, "A vistoria de retirada é obrigatória");
		this.estrategiaMulta = Objects.requireNonNull(estrategiaMulta, "A estratégia de cálculo de multa é obrigatória");
		this.status = Objects.requireNonNull(statusInicial, "O status inicial é obrigatório");
	}

	/**
	 * Construtor de conveniência que utiliza a estratégia de multa padrão.
	 */
	public Locacao(String codigo, Reserva reserva, Veiculo veiculo, int diasPrevistos, BigDecimal valorDiaria,
			ChecklistVistoria vistoriaRetirada) {
		this(codigo, reserva, veiculo, diasPrevistos, valorDiaria, vistoriaRetirada, new MultaPadraoStrategy());
	}

	public String getCodigo() {
		return codigo;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public StatusLocacao getStatus() {
		return status;
	}

	public ChecklistVistoria getVistoriaRetirada() {
		return vistoriaRetirada;
	}

	public ChecklistVistoria getVistoriaDevolucao() {
		return vistoriaDevolucao;
	}

	public int getDiasPrevistos() {
		return diasPrevistos;
	}

	public BigDecimal getValorDiaria() {
		return valorDiaria;
	}

	public CalculoMultaStrategy getEstrategiaMulta() {
		return estrategiaMulta;
	}

	public void registrarDevolucao(ChecklistVistoria vistoria) {
		this.vistoriaDevolucao = Objects.requireNonNull(vistoria, "A vistoria de devolução é obrigatória");
	}

	/**
	 * Processa a devolução do veículo e calcula o faturamento.
	 * Este é o método principal do Rich Domain Model para devolução.
	 * 
	 * <p>Este método encapsula toda a lógica de negócio da devolução:
	 * - Calcula dias utilizados e dias de atraso baseado na data real de devolução
	 * - Calcula todos os valores (diárias, atraso, multa, taxas)
	 * - Atualiza o estado da locação e do veículo
	 * </p>
	 * 
	 * @param vistoriaDevolucao Checklist da vistoria de devolução
	 * @param dataDevolucaoReal Data e hora real da devolução
	 * @param percentualMultaAtraso Percentual de multa a ser aplicado sobre o valor do atraso
	 * @return Faturamento calculado
	 * @throws IllegalStateException se a locação já foi finalizada
	 */
	public Faturamento realizarDevolucao(ChecklistVistoria vistoriaDevolucao, java.time.LocalDateTime dataDevolucaoReal, BigDecimal percentualMultaAtraso) {
		// 1. Validações de Negócio
		validarPodeSerDevolvida();
		
		// 2. Validar parâmetros
		this.vistoriaDevolucao = Objects.requireNonNull(vistoriaDevolucao, "Vistoria de devolução é obrigatória");
		Objects.requireNonNull(dataDevolucaoReal, "A data de devolução é obrigatória");
		Objects.requireNonNull(percentualMultaAtraso, "O percentual de multa por atraso é obrigatório");
		
		// 3. Calcular dias utilizados (data real de devolução vs data de retirada)
		var dataRetirada = reserva.getRetiradaInfo() != null 
			? reserva.getRetiradaInfo().dataHoraRetirada() 
			: reserva.getPeriodo().getRetirada();
		int diasUtilizados = calcularDiasUtilizados(dataRetirada, dataDevolucaoReal);
		
		// 4. Calcular dias de atraso (data real de devolução vs data prevista de devolução)
		var dataDevolucaoPrevista = reserva.getPeriodo().getDevolucao();
		int diasAtraso = calcularDiasAtraso(dataDevolucaoPrevista, dataDevolucaoReal);
		
		// 5. Calcular valores (regras de negócio na entidade)
		BigDecimal valorDiarias = calcularDiarias(diasUtilizados);
		BigDecimal valorAtraso = calcularValorAtraso(diasAtraso);
		BigDecimal multaAtraso = calcularMultaAtraso(valorAtraso, percentualMultaAtraso);
		BigDecimal taxasAdicionais = calcularTaxasAdicionais(vistoriaDevolucao);
		
		// 6. Calcular total
		BigDecimal valorTotal = valorDiarias
			.add(valorAtraso)
			.add(multaAtraso)
			.add(taxasAdicionais);
		
		// 7. Atualizar estado da locação (regra de negócio)
		this.status = StatusLocacao.FINALIZADA;
		
		// 8. Atualizar estado do veículo (regra de negócio)
		if (vistoriaDevolucao.possuiAvarias()) {
			veiculo.enviarParaManutencao();
		} else {
			var cidade = reserva.getCidadeRetirada();
			var patioDestino = new dev.sauloaraujo.sgb.dominio.locacao.patio.Patio(
				"PATIO-" + cidade.toUpperCase(), 
				cidade
			);
			veiculo.devolver(patioDestino);
		}
		
		// 9. Retornar faturamento
		return new Faturamento(valorTotal, valorDiarias, valorAtraso, multaAtraso, taxasAdicionais);
	}
	
	/**
	 * Calcula a quantidade de dias utilizados baseado na data de retirada e data real de devolução.
	 * 
	 * @param dataRetirada Data e hora da retirada
	 * @param dataDevolucaoReal Data e hora real da devolução
	 * @return Quantidade de dias utilizados (mínimo 1 dia)
	 */
	private int calcularDiasUtilizados(java.time.LocalDateTime dataRetirada, java.time.LocalDateTime dataDevolucaoReal) {
		var horasUtilizadas = java.time.Duration.between(dataRetirada, dataDevolucaoReal).toHours();
		var diasCalculados = (int) (horasUtilizadas / 24);
		if (horasUtilizadas % 24 != 0) {
			diasCalculados++;
		}
		return Math.max(1, diasCalculados);
	}
	
	/**
	 * Calcula a quantidade de dias de atraso comparando a data prevista com a data real de devolução.
	 * 
	 * @param dataDevolucaoPrevista Data prevista de devolução (da reserva)
	 * @param dataDevolucaoReal Data real de devolução
	 * @return Quantidade de dias de atraso (0 se não houver atraso)
	 */
	private int calcularDiasAtraso(java.time.LocalDateTime dataDevolucaoPrevista, java.time.LocalDateTime dataDevolucaoReal) {
		var horasAtraso = java.time.Duration.between(dataDevolucaoPrevista, dataDevolucaoReal).toHours();
		if (horasAtraso <= 0) {
			return 0; // Não há atraso
		}
		// Se a devolução real é posterior à prevista, há atraso
		int diasAtraso = (int) (horasAtraso / 24);
		if (horasAtraso % 24 != 0) {
			diasAtraso++; // Arredondar para cima
		}
		return diasAtraso;
	}
	
	
	/**
	 * Valida se a locação pode ser devolvida.
	 */
	private void validarPodeSerDevolvida() {
		if (this.status == StatusLocacao.FINALIZADA) {
			throw new IllegalStateException("Locação " + this.codigo + " já foi finalizada");
		}
		if (this.status != StatusLocacao.EM_ANDAMENTO) {
			throw new IllegalStateException("Locação " + this.codigo + " não está em andamento. Status atual: " + this.status);
		}
	}
	
	/**
	 * Calcula taxas adicionais baseadas na vistoria de devolução.
	 */
	private BigDecimal calcularTaxasAdicionais(ChecklistVistoria vistoria) {
		BigDecimal taxas = BigDecimal.ZERO;
		
		// Taxa de combustível (se não devolveu com tanque cheio)
		if (vistoria.combustivel() != null && !vistoria.combustivel().equals("CHEIO")) {
			taxas = taxas.add(new BigDecimal("50.00"));
		}
		
		// Taxa de limpeza/reparo (se houver avarias)
		if (vistoria.possuiAvarias()) {
			taxas = taxas.add(new BigDecimal("100.00"));
		}
		
		return taxas;
	}

	public Faturamento finalizar(int diasUtilizados, int diasAtraso, BigDecimal percentualMultaAtraso,
			BigDecimal taxaCombustivel, boolean enviarParaManutencao) {
		if (status == StatusLocacao.FINALIZADA) {
			throw new IllegalStateException("A locação já foi finalizada");
		}
		if (status != StatusLocacao.EM_ANDAMENTO) {
			throw new IllegalStateException("A locação não está em andamento. Status atual: " + status);
		}

		var diarias = calcularDiarias(diasUtilizados);
		var valorAtraso = calcularValorAtraso(diasAtraso);
		var multaAtraso = calcularMultaAtraso(valorAtraso, percentualMultaAtraso);
		var taxa = taxaCombustivel == null ? BigDecimal.ZERO : taxaCombustivel;

		var total = diarias.add(valorAtraso).add(multaAtraso).add(taxa);

		status = StatusLocacao.FINALIZADA;
		if (enviarParaManutencao) {
			veiculo.enviarParaManutencao();
		} else {
			var cidade = reserva.getCidadeRetirada();
			var patioDestino = new dev.sauloaraujo.sgb.dominio.locacao.patio.Patio(
					"PATIO-" + cidade.toUpperCase(), cidade);
			veiculo.devolver(patioDestino);
		}

		return new Faturamento(total, diarias, valorAtraso, multaAtraso, taxa);
	}

	private BigDecimal calcularDiarias(int dias) {
		var diasConsiderados = Math.max(dias, diasPrevistos);
		return valorDiaria.multiply(BigDecimal.valueOf(diasConsiderados));
	}

	private BigDecimal calcularValorAtraso(int diasAtraso) {
		if (diasAtraso <= 0) {
			return BigDecimal.ZERO;
		}
		return valorDiaria.multiply(BigDecimal.valueOf(diasAtraso));
	}

	private BigDecimal calcularMultaAtraso(BigDecimal valorAtraso, BigDecimal percentualMultaAtraso) {
		return estrategiaMulta.calcular(valorAtraso, percentualMultaAtraso);
	}
}
