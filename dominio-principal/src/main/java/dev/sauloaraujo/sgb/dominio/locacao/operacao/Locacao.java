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
	private StatusLocacao status;
	private ChecklistVistoria vistoriaRetirada;
	private ChecklistVistoria vistoriaDevolucao;

	public Locacao(String codigo, Reserva reserva, Veiculo veiculo, int diasPrevistos, BigDecimal valorDiaria,
			ChecklistVistoria vistoriaRetirada) {
		this.codigo = Objects.requireNonNull(codigo, "O código da locação é obrigatório");
		this.reserva = Objects.requireNonNull(reserva, "A reserva é obrigatória");
		this.veiculo = Objects.requireNonNull(veiculo, "O veículo é obrigatório");
		if (diasPrevistos <= 0) {
			throw new IllegalArgumentException("Dias previstos devem ser positivos");
		}
		this.diasPrevistos = diasPrevistos;
		this.valorDiaria = Objects.requireNonNull(valorDiaria, "O valor da diária é obrigatório");
		this.vistoriaRetirada = Objects.requireNonNull(vistoriaRetirada, "A vistoria de retirada é obrigatória");
		this.status = StatusLocacao.ATIVA;
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

	public void registrarDevolucao(ChecklistVistoria vistoria) {
		this.vistoriaDevolucao = Objects.requireNonNull(vistoria, "A vistoria de devolução é obrigatória");
	}

	public Faturamento finalizar(int diasUtilizados, int diasAtraso, BigDecimal percentualMultaAtraso,
			BigDecimal taxaCombustivel, boolean enviarParaManutencao) {
		if (!status.ativa()) {
			throw new IllegalStateException("A locação já foi finalizada");
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
			veiculo.devolver();
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
		if (valorAtraso.signum() <= 0 || percentualMultaAtraso == null) {
			return BigDecimal.ZERO;
		}
		return valorAtraso.multiply(percentualMultaAtraso);
	}
}
