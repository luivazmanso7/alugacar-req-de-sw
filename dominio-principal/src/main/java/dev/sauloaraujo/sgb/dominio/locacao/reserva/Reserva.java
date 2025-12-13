package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.RetiradaInfo;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;

public class Reserva {
	private final String codigo;
	private final CategoriaCodigo categoria;
	private final String cidadeRetirada;
	private PeriodoLocacao periodo;
	private final Cliente cliente;
	private BigDecimal valorEstimado;
	private StatusReserva status;
	private final String placaVeiculo;
	private RetiradaInfo retiradaInfo;

	public Reserva(CategoriaCodigo categoria, String cidadeRetirada, PeriodoLocacao periodo, BigDecimal valorEstimado,
			Cliente cliente, String placaVeiculo) {
		this(UUID.randomUUID().toString(), categoria, cidadeRetirada, periodo, valorEstimado, StatusReserva.ATIVA,
				cliente, placaVeiculo);
	}

	public Reserva(String codigo, CategoriaCodigo categoria, String cidadeRetirada, PeriodoLocacao periodo,
			BigDecimal valorEstimado, StatusReserva status, Cliente cliente, String placaVeiculo) {
		this(codigo, categoria, cidadeRetirada, periodo, valorEstimado, status, cliente, placaVeiculo, null);
	}
	
	public Reserva(String codigo, CategoriaCodigo categoria, String cidadeRetirada, PeriodoLocacao periodo,
			BigDecimal valorEstimado, StatusReserva status, Cliente cliente, String placaVeiculo, RetiradaInfo retiradaInfo) {
		this.codigo = Objects.requireNonNull(codigo, "O código da reserva é obrigatório");
		this.categoria = Objects.requireNonNull(categoria, "A categoria é obrigatória");
		this.cidadeRetirada = Objects.requireNonNull(cidadeRetirada, "A cidade de retirada é obrigatória");
		this.periodo = Objects.requireNonNull(periodo, "O período é obrigatório");
		this.valorEstimado = Objects.requireNonNull(valorEstimado, "O valor estimado é obrigatório");
		if (valorEstimado.signum() <= 0) {
			throw new IllegalArgumentException("O valor estimado deve ser positivo");
		}
		this.status = Objects.requireNonNull(status, "O status é obrigatório");
		this.cliente = Objects.requireNonNull(cliente, "O cliente da reserva é obrigatório");
		this.placaVeiculo = Objects.requireNonNull(placaVeiculo, "A placa do veículo é obrigatória");
		if (placaVeiculo.isBlank()) {
			throw new IllegalArgumentException("A placa do veículo não pode estar vazia");
		}
		this.retiradaInfo = retiradaInfo;
	}

	public String getCodigo() {
		return codigo;
	}

	public CategoriaCodigo getCategoria() {
		return categoria;
	}

	public String getCidadeRetirada() {
		return cidadeRetirada;
	}

	public PeriodoLocacao getPeriodo() {
		return periodo;
	}

	public BigDecimal getValorEstimado() {
		return valorEstimado;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public StatusReserva getStatus() {
		return status;
	}

	public String getPlacaVeiculo() {
		return placaVeiculo;
	}
	
	public RetiradaInfo getRetiradaInfo() {
		return retiradaInfo;
	}

	public long diasReservados() {
		return periodo.dias();
	}

	public void concluir() {
		if (status != StatusReserva.ATIVA) {
			throw new IllegalStateException(
				"Só é possível concluir reservas ATIVAS. Status atual: " + status
			);
		}
		status = StatusReserva.CONCLUIDA;
	}

	public void manterAtiva() {
		status = StatusReserva.ATIVA;
	}

	public void cancelar() {
		if (status != StatusReserva.ATIVA) {
			throw new IllegalStateException(
				"Só é possível cancelar reservas ATIVAS. Status atual: " + status
			);
		}
		status = StatusReserva.CANCELADA;
	}
	
	public void confirmarRetirada(Veiculo veiculo, RetiradaInfo info) {
		if (status != StatusReserva.ATIVA) {
			throw new IllegalStateException(
				"Só é possível confirmar retirada de reservas ATIVAS. Status atual: " + status
			);
		}
		
		Objects.requireNonNull(veiculo, "O veículo é obrigatório");
		Objects.requireNonNull(info, "As informações da retirada são obrigatórias");
		
		if (!veiculo.getPlaca().equals(this.placaVeiculo)) {
			throw new IllegalArgumentException(
				"A placa do veículo não corresponde à placa da reserva"
			);
		}
		
		if (!info.placaVeiculo().equals(this.placaVeiculo)) {
			throw new IllegalArgumentException(
				"A placa informada nas informações da retirada não corresponde à placa da reserva"
			);
		}
		
		if (!veiculo.getCategoria().equals(this.categoria)) {
			throw new IllegalArgumentException(
				"A categoria do veículo não corresponde à categoria da reserva"
			);
		}
		
		this.retiradaInfo = info;
		this.status = StatusReserva.EM_ANDAMENTO;
	}

	public void cancelarComTarifa(BigDecimal tarifa) {
		cancelar();
	}

	public void ajustarValorEstimado(BigDecimal diaria) {
		Objects.requireNonNull(diaria, "A diária é obrigatória");
		if (diaria.signum() <= 0) {
			throw new IllegalArgumentException("A diária deve ser positiva");
		}

		this.valorEstimado = diaria.multiply(BigDecimal.valueOf(periodo.dias()));
	}

	public void replanejar(PeriodoLocacao novoPeriodo, BigDecimal diaria) {
		if (status != StatusReserva.ATIVA) {
			throw new IllegalStateException(
				"Só é possível replanejar reservas ATIVAS. Status atual: " + status
			);
		}
		this.periodo = Objects.requireNonNull(novoPeriodo, "O período é obrigatório");
		ajustarValorEstimado(diaria);
	}
}
