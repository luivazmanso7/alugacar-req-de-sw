package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
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

	public Reserva(CategoriaCodigo categoria, String cidadeRetirada, PeriodoLocacao periodo, BigDecimal valorEstimado,
			Cliente cliente) {
		this(UUID.randomUUID().toString(), categoria, cidadeRetirada, periodo, valorEstimado, StatusReserva.ATIVA,
				cliente);
	}

	public Reserva(String codigo, CategoriaCodigo categoria, String cidadeRetirada, PeriodoLocacao periodo,
			BigDecimal valorEstimado, StatusReserva status, Cliente cliente) {
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

	public long diasReservados() {
		return periodo.dias();
	}

	public void concluir() {
		status = StatusReserva.CONCLUIDA;
	}

	public void manterAtiva() {
		status = StatusReserva.ATIVA;
	}

	public void cancelar() {
		status = StatusReserva.CANCELADA;
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
		this.periodo = Objects.requireNonNull(novoPeriodo, "O período é obrigatório");
		ajustarValorEstimado(diaria);
	}
}
