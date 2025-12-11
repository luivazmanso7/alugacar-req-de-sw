package dev.sauloaraujo.sgb.apresentacao.locacao.catalogo;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.catalogo.VeiculoResumo;
import dev.sauloaraujo.sgb.aplicacao.locacao.catalogo.VeiculoServicoAplicacao;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para operações de consulta de veículos.
 */
@RestController
@RequestMapping("/veiculos")
@Tag(name = "Veículos", description = "Operações de consulta de veículos")
public class VeiculoController {

    private final VeiculoServicoAplicacao veiculoServico;

    public VeiculoController(VeiculoServicoAplicacao veiculoServico) {
        this.veiculoServico = veiculoServico;
    }

    /**
     * Busca um veículo por placa.
     */
    @GetMapping("/{placa}")
    @Operation(summary = "Buscar veículo por placa",
               description = "Retorna um veículo específico pela placa")
    public ResponseEntity<VeiculoResumo> buscarPorPlaca(
            @Parameter(description = "Placa do veículo") @PathVariable String placa) {
        return veiculoServico.buscarPorPlaca(placa)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca veículos disponíveis por cidade e categoria.
     */
    @GetMapping("/disponiveis")
    @Operation(summary = "Buscar veículos disponíveis",
               description = "Retorna veículos disponíveis filtrados por cidade e categoria (opcional)")
    public ResponseEntity<List<VeiculoResumo>> buscarDisponiveis(
            @Parameter(description = "Cidade para busca") @RequestParam String cidade,
            @Parameter(description = "Categoria do veículo (opcional)") 
            @RequestParam(required = false) String categoria) {
        
        List<VeiculoResumo> veiculos;
        
        if (categoria != null && !categoria.isBlank()) {
            try {
                var categoriaCodigo = CategoriaCodigo.valueOf(categoria.toUpperCase());
                veiculos = veiculoServico.buscarDisponiveis(cidade, categoriaCodigo);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            veiculos = veiculoServico.buscarDisponiveis(cidade);
        }
        
        return ResponseEntity.ok(veiculos);
    }
}

