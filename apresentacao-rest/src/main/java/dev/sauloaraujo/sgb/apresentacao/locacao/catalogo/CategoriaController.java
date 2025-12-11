package dev.sauloaraujo.sgb.apresentacao.locacao.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.catalogo.CategoriaResumo;
import dev.sauloaraujo.sgb.aplicacao.locacao.catalogo.CategoriaServicoAplicacao;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para operações de consulta de categorias.
 */
@RestController
@RequestMapping("/categorias")
@Tag(name = "Categorias", description = "Operações de consulta de categorias de veículos")
public class CategoriaController {

    private final CategoriaServicoAplicacao categoriaServico;

    public CategoriaController(CategoriaServicoAplicacao categoriaServico) {
        this.categoriaServico = categoriaServico;
    }

    /**
     * Lista todas as categorias disponíveis.
     */
    @GetMapping
    @Operation(summary = "Listar categorias",
               description = "Retorna todas as categorias de veículos cadastradas")
    public ResponseEntity<List<CategoriaResumo>> listar() {
        var categorias = categoriaServico.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Busca uma categoria específica por código.
     */
    @GetMapping("/{codigo}")
    @Operation(summary = "Buscar categoria por código",
               description = "Retorna uma categoria específica pelo código")
    public ResponseEntity<CategoriaResumo> buscarPorCodigo(
            @Parameter(description = "Código da categoria") @PathVariable String codigo) {
        try {
            var categoriaCodigo = CategoriaCodigo.valueOf(codigo.toUpperCase());
            return categoriaServico.buscarPorCodigo(categoriaCodigo)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

