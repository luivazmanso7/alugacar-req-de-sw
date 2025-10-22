package dev.sauloaraujo.sgb.dominio.locacao.manutencao;

import java.time.LocalDateTime;
import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;

public class ManutencaoServico {
	private final VeiculoRepositorio veiculoRepositorio;

	public ManutencaoServico(VeiculoRepositorio veiculoRepositorio) {
		this.veiculoRepositorio = Objects.requireNonNull(veiculoRepositorio, "Repositorio de veículos é obrigatório");
	}

	public Veiculo agendar(String placa, LocalDateTime previsao, String motivo) {
		var veiculo = veiculoRepositorio.buscarPorPlaca(placa)
				.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

		veiculo.agendarManutencao(previsao, motivo);
		veiculoRepositorio.salvar(veiculo);
		return veiculo;
	}
}
