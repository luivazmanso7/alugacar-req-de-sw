package dev.sauloaraujo.sgb.apresentacao.locacao.categoria;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.categoria.CategoriaResumo;
import dev.sauloaraujo.sgb.aplicacao.locacao.categoria.CategoriaServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para operações de consulta de Categorias de Veículos.
 * Expõe endpoints para listar e buscar categorias.
 */
@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorias", description = "Operações de consulta de categorias de veículos")
public class CategoriaController {
	
	private final CategoriaServicoAplicacao categoriaServico;

	public CategoriaController(CategoriaServicoAplicacao categoriaServico) {
		this.categoriaServico = categoriaServico;
	}

	/**
	 * Lista todas as categorias disponíveis.
	 * 
	 * @return Lista de categorias
	 */
	@GetMapping
	@Operation(summary = "Listar categorias", description = "Retorna todas as categorias de veículos disponíveis")
	public ResponseEntity<List<CategoriaResumo>> listar() {
		var categorias = categoriaServico.pesquisarResumos();
		return ResponseEntity.ok(categorias);
	}

	/**
	 * Busca uma categoria específica por código.
	 * 
	 * @param codigo Código da categoria
	 * @return Categoria encontrada ou 404 se não existir
	 */
	@GetMapping("/{codigo}")
	@Operation(summary = "Buscar categoria por código", description = "Retorna uma categoria específica pelo código")
	public ResponseEntity<CategoriaResumo> buscarPorCodigo(@PathVariable String codigo) {
		return categoriaServico.buscarPorCodigo(codigo)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
