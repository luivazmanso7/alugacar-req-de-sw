package dev.sauloaraujo.sgb.apresentacao.locacao.reserva;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ReservaResumo;
import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ReservaServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para operações de consulta de Reservas.
 * Expõe endpoints para listar, buscar e filtrar reservas.
 */
@RestController
@RequestMapping("/api/v1/reservas")
@Tag(name = "Reservas", description = "Operações de consulta de reservas")
public class ReservaController {
	
	private final ReservaServicoAplicacao reservaServico;

	public ReservaController(ReservaServicoAplicacao reservaServico) {
		this.reservaServico = reservaServico;
	}

	/**
	 * Lista todas as reservas.
	 * 
	 * @return Lista de reservas
	 */
	@GetMapping
	@Operation(summary = "Listar reservas", description = "Retorna todas as reservas cadastradas")
	public ResponseEntity<List<ReservaResumo>> listar() {
		var reservas = reservaServico.pesquisarResumos();
		return ResponseEntity.ok(reservas);
	}

	/**
	 * Busca uma reserva específica por código.
	 * 
	 * @param codigo Código da reserva
	 * @return Reserva encontrada ou 404 se não existir
	 */
	@GetMapping("/{codigo}")
	@Operation(summary = "Buscar reserva por código", description = "Retorna uma reserva específica pelo código")
	public ResponseEntity<ReservaResumo> buscarPorCodigo(@PathVariable String codigo) {
		return reservaServico.buscarPorCodigo(codigo)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Lista reservas de um cliente específico.
	 * 
	 * @param cpfOuCnpj CPF ou CNPJ do cliente
	 * @return Lista de reservas do cliente
	 */
	@GetMapping("/cliente/{cpfOuCnpj}")
	@Operation(summary = "Listar reservas por cliente", 
	           description = "Retorna todas as reservas de um cliente específico")
	public ResponseEntity<List<ReservaResumo>> listarPorCliente(
			@Parameter(description = "CPF ou CNPJ do cliente") @PathVariable String cpfOuCnpj) {
		var reservas = reservaServico.listarPorCliente(cpfOuCnpj);
		return ResponseEntity.ok(reservas);
	}

	/**
	 * Lista apenas as reservas ativas.
	 * 
	 * @return Lista de reservas ativas
	 */
	@GetMapping("/ativas")
	@Operation(summary = "Listar reservas ativas", description = "Retorna todas as reservas com status ativo")
	public ResponseEntity<List<ReservaResumo>> listarAtivas() {
		var reservas = reservaServico.listarAtivas();
		return ResponseEntity.ok(reservas);
	}
}
