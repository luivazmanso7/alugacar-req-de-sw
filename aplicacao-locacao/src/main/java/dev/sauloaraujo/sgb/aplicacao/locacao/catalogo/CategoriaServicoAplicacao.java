package dev.sauloaraujo.sgb.aplicacao.locacao.catalogo;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaRepositorio;

/**
 * Serviço de aplicação para operações de consulta de categorias.
 */
@Service
public class CategoriaServicoAplicacao {

    private final CategoriaRepositorio categoriaRepositorio;

    public CategoriaServicoAplicacao(CategoriaRepositorio categoriaRepositorio) {
        this.categoriaRepositorio = notNull(categoriaRepositorio, 
            "Repositório de categorias não pode ser nulo");
    }

    @Transactional(readOnly = true)
    public List<CategoriaResumo> listarTodas() {
        var categorias = categoriaRepositorio.listarTodas();
        return categorias.stream()
                .map(this::toResumo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CategoriaResumo> buscarPorCodigo(CategoriaCodigo codigo) {
        return categoriaRepositorio.buscarPorCodigo(codigo)
                .map(this::toResumo);
    }

    private CategoriaResumo toResumo(Categoria categoria) {
        return new CategoriaResumo(
                categoria.getCodigo().name(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getDiaria(),
                categoria.getQuantidadeDisponivel()
        );
    }
}

