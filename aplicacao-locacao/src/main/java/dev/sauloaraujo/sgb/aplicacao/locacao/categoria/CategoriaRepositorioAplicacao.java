package dev.sauloaraujo.sgb.aplicacao.locacao.categoria;

import java.util.List;
import java.util.Optional;

/**
 * Interface do Repositório de Aplicação para Categoria.
 * Define os métodos de consulta necessários para a camada de aplicação.
 */
public interface CategoriaRepositorioAplicacao {
	List<CategoriaResumo> pesquisarResumos();
	
	Optional<CategoriaResumo> buscarPorCodigo(String codigo);
}
