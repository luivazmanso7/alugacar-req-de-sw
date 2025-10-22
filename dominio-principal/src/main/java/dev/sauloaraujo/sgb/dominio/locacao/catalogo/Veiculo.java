package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;

public class Veiculo {
	private final String placa;
	private final String modelo;
	private final CategoriaCodigo categoria;
	private final String cidade;
	private final BigDecimal diaria;
	private StatusVeiculo status;
	private LocalDateTime manutencaoPrevista;
	private String manutencaoNota;

	public Veiculo(String placa, String modelo, CategoriaCodigo categoria, String cidade, BigDecimal diaria,
			StatusVeiculo status) {
		this.placa = validarPlaca(placa);
		this.modelo = Objects.requireNonNull(modelo, "O modelo é obrigatório");
		this.categoria = Objects.requireNonNull(categoria, "A categoria é obrigatória");
		this.cidade = Objects.requireNonNull(cidade, "A cidade é obrigatória");
		this.diaria = Objects.requireNonNull(diaria, "O valor da diária é obrigatório");
		if (diaria.signum() <= 0) {
			throw new IllegalArgumentException("O valor da diária deve ser positivo");
		}

		this.status = Objects.requireNonNull(status, "O status do veículo é obrigatório");
	}

	private String validarPlaca(String placa) {
		var valor = Objects.requireNonNull(placa, "A placa é obrigatória").trim();
		if (valor.isEmpty()) {
			throw new IllegalArgumentException("A placa é obrigatória");
		}
		return valor.toUpperCase();
	}

	public String getPlaca() {
		return placa;
	}

	public String getModelo() {
		return modelo;
	}

	public CategoriaCodigo getCategoria() {
		return categoria;
	}

	public String getCidade() {
		return cidade;
	}

	public BigDecimal getDiaria() {
		return diaria;
	}

	public StatusVeiculo getStatus() {
		return status;
	}

	public LocalDateTime getManutencaoPrevista() {
		return manutencaoPrevista;
	}

	public String getManutencaoNota() {
		return manutencaoNota;
	}

	public boolean disponivel() {
		return status.disponivel();
	}

	public void reservar() {
		if (!status.disponivel()) {
			throw new IllegalStateException("O veículo não está disponível para reserva");
		}
		status = StatusVeiculo.RESERVADO;
	}

	public void locar() {
		if (status == StatusVeiculo.LOCADO) {
			throw new IllegalStateException("O veículo já está locado");
		}
		status = StatusVeiculo.LOCADO;
	}

	public void devolver() {
		status = StatusVeiculo.DISPONIVEL;
	}

	public void enviarParaManutencao() {
		status = StatusVeiculo.EM_MANUTENCAO;
	}

	public void agendarManutencao(LocalDateTime previsao, String nota) {
		manutencaoNota = Objects.requireNonNull(nota, "O motivo da manutenção é obrigatório");
		manutencaoPrevista = Objects.requireNonNull(previsao, "A data prevista da manutenção é obrigatória");

		if (!status.disponivel()) {
			throw new IllegalStateException("Veículo não pode entrar em manutenção enquanto reservado ou locado");
		}

		status = StatusVeiculo.EM_MANUTENCAO;
	}
}
