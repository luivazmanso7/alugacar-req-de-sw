package dev.sauloaraujo.sgb.aplicacao.locacao.categoria;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de Aplicação para Categoria.
 * Coordena operações relacionadas a categorias de veículos na camada de aplicação.
 */
public class CategoriaServicoAplicacao {
	private final CategoriaRepositorioAplicacao repositorio;

	public CategoriaServicoAplicacao(CategoriaRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<CategoriaResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
	
	public Optional<CategoriaResumo> buscarPorCodigo(String codigo) {
		notNull(codigo, "O código não pode ser nulo");
		return repositorio.buscarPorCodigo(codigo);
	}
}
