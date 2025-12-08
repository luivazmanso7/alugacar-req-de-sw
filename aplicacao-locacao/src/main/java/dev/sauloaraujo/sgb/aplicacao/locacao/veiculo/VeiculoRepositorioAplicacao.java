package dev.sauloaraujo.sgb.aplicacao.locacao.veiculo;

import java.util.List;
import java.util.Optional;

/**
 * Interface do Repositório de Aplicação para Veículo.
 * Define os métodos de consulta necessários para a camada de aplicação.
 */
public interface VeiculoRepositorioAplicacao {
	List<VeiculoResumo> pesquisarResumos();
	
	Optional<VeiculoResumo> buscarPorPlaca(String placa);
	
	List<VeiculoResumo> listarDisponiveis(String cidade, String categoria);
}
