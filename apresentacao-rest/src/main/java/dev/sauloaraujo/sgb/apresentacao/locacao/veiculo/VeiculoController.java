package dev.sauloaraujo.sgb.apresentacao.locacao.veiculo;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.veiculo.VeiculoResumo;
import dev.sauloaraujo.sgb.aplicacao.locacao.veiculo.VeiculoServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para operações de consulta de Veículos.
 * Expõe endpoints para listar, buscar e filtrar veículos.
 */
@RestController
@RequestMapping("/api/v1/veiculos")
@Tag(name = "Veículos", description = "Operações de consulta de veículos")
public class VeiculoController {
	
	private final VeiculoServicoAplicacao veiculoServico;

	public VeiculoController(VeiculoServicoAplicacao veiculoServico) {
		this.veiculoServico = veiculoServico;
	}

	/**
	 * Lista todos os veículos.
	 * 
	 * @return Lista de veículos
	 */
	@GetMapping
	@Operation(summary = "Listar veículos", description = "Retorna todos os veículos cadastrados")
	public ResponseEntity<List<VeiculoResumo>> listar() {
		var veiculos = veiculoServico.pesquisarResumos();
		return ResponseEntity.ok(veiculos);
	}

	/**
	 * Busca um veículo específico por placa.
	 * 
	 * @param placa Placa do veículo
	 * @return Veículo encontrado ou 404 se não existir
	 */
	@GetMapping("/{placa}")
	@Operation(summary = "Buscar veículo por placa", description = "Retorna um veículo específico pela placa")
	public ResponseEntity<VeiculoResumo> buscarPorPlaca(@PathVariable String placa) {
		return veiculoServico.buscarPorPlaca(placa)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Lista veículos disponíveis para locação.
	 * 
	 * @param cidade Cidade de retirada
	 * @param categoria Código da categoria desejada
	 * @return Lista de veículos disponíveis
	 */
	@GetMapping("/disponiveis")
	@Operation(summary = "Listar veículos disponíveis", 
	           description = "Retorna veículos disponíveis para locação em uma cidade e categoria específicas")
	public ResponseEntity<List<VeiculoResumo>> listarDisponiveis(
			@Parameter(description = "Cidade de retirada") @RequestParam String cidade,
			@Parameter(description = "Código da categoria") @RequestParam String categoria) {
		var veiculos = veiculoServico.listarDisponiveis(cidade, categoria);
		return ResponseEntity.ok(veiculos);
	}
}
