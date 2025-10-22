package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import java.util.List;
import java.util.Optional;

public interface VeiculoRepositorio {
	void salvar(Veiculo veiculo);

	Optional<Veiculo> buscarPorPlaca(String placa);

	List<Veiculo> buscarDisponiveis(String cidade, CategoriaCodigo categoria);

	List<Veiculo> buscarDisponiveis(String cidade);
}
