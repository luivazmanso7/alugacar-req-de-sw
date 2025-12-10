package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.patio.Patio;
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
	private Patio patio;

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
		this.patio = new Patio("PATIO-" + this.cidade.toUpperCase(), this.cidade);
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

	public Patio getPatio() {
		return patio;
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
		if (status == StatusVeiculo.VENDIDO) {
			throw new IllegalStateException("Veículo vendido não pode ser reservado");
		}
		status = StatusVeiculo.RESERVADO;
	}

	public void locar() {
		if (status == StatusVeiculo.LOCADO) {
			throw new IllegalStateException("O veículo já está locado");
		}
		if (status == StatusVeiculo.VENDIDO) {
			throw new IllegalStateException("Veículo vendido não pode ser locado");
		}
		status = StatusVeiculo.LOCADO;
		removerDoPatio();
	}

	public void devolver(Patio patioDestino) {
		status = StatusVeiculo.DISPONIVEL;
		this.patio = Objects.requireNonNull(patioDestino, "O pátio é obrigatório");
	}

	public void enviarParaManutencao() {
		status = StatusVeiculo.EM_MANUTENCAO;
		removerDoPatio();
	}

	public void agendarManutencao(LocalDateTime previsao, String nota) {
		manutencaoNota = Objects.requireNonNull(nota, "O motivo da manutenção é obrigatório");
		manutencaoPrevista = Objects.requireNonNull(previsao, "A data prevista da manutenção é obrigatória");

		if (status == StatusVeiculo.LOCADO) {
			throw new IllegalStateException("Veículo não pode entrar em manutenção enquanto reservado ou locado");
		}
		if (status == StatusVeiculo.VENDIDO) {
			throw new IllegalStateException("Veículo vendido não pode entrar em manutenção");
		}

		status = StatusVeiculo.EM_MANUTENCAO;
		removerDoPatio();
	}

	public void removerDoPatio() {
		patio = null;
	}
}
