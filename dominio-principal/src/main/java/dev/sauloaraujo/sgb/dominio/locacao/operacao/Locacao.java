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
	 * @param vistoriaDevolucao Checklist da vistoria de devolução
	 * @param diasUtilizados Quantidade de dias que o veículo foi utilizado
	 * @return Faturamento calculado
	 * @throws IllegalStateException se a locação já foi finalizada
	 */
	public Faturamento realizarDevolucao(ChecklistVistoria vistoriaDevolucao, int diasUtilizados) {
		// 1. Validações de Negócio
		validarPodeSerDevolvida();
		
		// 2. Registrar vistoria
		this.vistoriaDevolucao = Objects.requireNonNull(vistoriaDevolucao, "Vistoria de devolução é obrigatória");
		
		// 3. Calcular atraso
		int diasAtraso = Math.max(0, diasUtilizados - diasPrevistos);
		
		// 4. Calcular valores
		BigDecimal valorDiarias = calcularDiarias(diasUtilizados);
		BigDecimal valorAtraso = calcularValorAtraso(diasAtraso);
		BigDecimal multaAtraso = calcularMultaAtraso(valorAtraso, new BigDecimal("0.10")); // 10% de multa
		BigDecimal taxasAdicionais = calcularTaxasAdicionais(vistoriaDevolucao);
		
		// 5. Calcular total
		BigDecimal valorTotal = valorDiarias
			.add(valorAtraso)
			.add(multaAtraso)
			.add(taxasAdicionais);
		
		// 6. Atualizar estado da locação
		this.status = StatusLocacao.FINALIZADA;
		
		// 7. Atualizar estado do veículo (Regra de Negócio)
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
		
		// 8. Retornar faturamento
		return new Faturamento(valorTotal, valorDiarias, valorAtraso, multaAtraso, taxasAdicionais);
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
