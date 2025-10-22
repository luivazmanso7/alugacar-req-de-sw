package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepositorio {
	void salvar(Categoria categoria);

	Optional<Categoria> buscarPorCodigo(CategoriaCodigo codigo);

	List<Categoria> listarTodas();
}
