package dev.sauloaraujo.sgb.aplicacao.locacao.veiculo;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de Aplicação para Veículo.
 * Coordena operações relacionadas a veículos na camada de aplicação.
 */
public class VeiculoServicoAplicacao {
	private final VeiculoRepositorioAplicacao repositorio;

	public VeiculoServicoAplicacao(VeiculoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<VeiculoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
	
	public Optional<VeiculoResumo> buscarPorPlaca(String placa) {
		notNull(placa, "A placa não pode ser nula");
		return repositorio.buscarPorPlaca(placa);
	}
	
	public List<VeiculoResumo> listarDisponiveis(String cidade, String categoria) {
		notNull(cidade, "A cidade não pode ser nula");
		notNull(categoria, "A categoria não pode ser nula");
		return repositorio.listarDisponiveis(cidade, categoria);
	}
}
