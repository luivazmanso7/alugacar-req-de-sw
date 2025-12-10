package dev.sauloaraujo.sgb.dominio.locacao.manutencao;

import java.time.LocalDateTime;
import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.evento.VeiculoFoiParaManutencaoEvent;

public class ManutencaoServico {
	private final VeiculoRepositorio veiculoRepositorio;

	public ManutencaoServico(VeiculoRepositorio veiculoRepositorio) {
		this.veiculoRepositorio = Objects.requireNonNull(veiculoRepositorio, "Repositorio de veículos é obrigatório");
	}

	public VeiculoFoiParaManutencaoEvent agendar(String placa, LocalDateTime previsao, String motivo) {
		Objects.requireNonNull(placa, "Placa do veículo é obrigatória");
		Objects.requireNonNull(previsao, "A previsão de término é obrigatória");
		Objects.requireNonNull(motivo, "O motivo da manutenção é obrigatório");

		var veiculo = veiculoRepositorio.buscarPorPlaca(placa)
				.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

		veiculo.agendarManutencao(previsao, motivo);
		veiculoRepositorio.salvar(veiculo);

		return new VeiculoFoiParaManutencaoEvent(
				veiculo.getPlaca(),
				veiculo.getCategoria().name(),
				motivo,
				LocalDateTime.now(),
				previsao);
	}
}
