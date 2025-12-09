package dev.sauloaraujo.sgb.apresentacao.locacao.reserva;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ReservaResumo;
import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ReservaServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para operações de consulta e gerenciamento de Reservas.
 * Expõe endpoints para listar, buscar, criar, alterar e cancelar reservas.
 */
@RestController
@RequestMapping("/api/v1/reservas")
@Tag(name = "Reservas", description = "Operações de consulta e gerenciamento de reservas")
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
	
	/**
	 * Cria uma nova reserva.
	 * 
	 * @param request Dados da reserva
	 * @return Reserva criada (201 Created)
	 */
	@PostMapping
	@Operation(
		summary = "Criar reserva", 
		description = "Cria uma nova reserva para um cliente autenticado"
	)
	public ResponseEntity<ReservaResponse> criar(@Valid @RequestBody CriarReservaRequest request) {
		var reserva = reservaServico.criarReserva(
			request.cpfCliente(),
			request.categoria(),
			request.cidadeRetirada(),
			request.dataRetirada(),
			request.dataDevolucao()
		);
		
		var response = new ReservaResponse(
			reserva.getCodigo(),
			"Reserva criada com sucesso",
			reserva
		);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	/**
	 * Confirma a retirada de uma reserva (inicia a locação).
	 * 
	 * @param codigo Código da reserva
	 * @param request Dados de vistoria
	 * @return Confirmação da retirada
	 */
	@PostMapping("/{codigo}/confirmar-retirada")
	@Operation(
		summary = "Confirmar retirada", 
		description = "Confirma a retirada do veículo e inicia a locação"
	)
	public ResponseEntity<ReservaResponse> confirmarRetirada(
			@PathVariable String codigo,
			@Valid @RequestBody ConfirmarRetiradaRequest request) {
		
		reservaServico.confirmarRetirada(
			codigo,
			request.placaVeiculo(),
			request.quilometragem(),
			request.combustivel(),
			request.possuiAvarias()
		);
		
		var response = new ReservaResponse(
			codigo,
			"Retirada confirmada com sucesso",
			null
		);
		
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Altera o período de uma reserva.
	 * 
	 * @param codigo Código da reserva
	 * @param request Novo período
	 * @return Reserva alterada
	 */
	@PutMapping("/{codigo}")
	@Operation(
		summary = "Alterar reserva", 
		description = "Altera o período de uma reserva existente (replanejamento)"
	)
	public ResponseEntity<ReservaResponse> alterar(
			@PathVariable String codigo,
			@Valid @RequestBody AlterarReservaRequest request) {
		
		reservaServico.alterarReserva(
			codigo,
			request.novaDataRetirada(),
			request.novaDataDevolucao()
		);
		
		var response = new ReservaResponse(
			codigo,
			"Reserva alterada com sucesso",
			null
		);
		
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Cancela uma reserva.
	 * 
	 * @param codigo Código da reserva
	 * @return Confirmação do cancelamento
	 */
	@DeleteMapping("/{codigo}")
	@Operation(
		summary = "Cancelar reserva", 
		description = "Cancela uma reserva existente, aplicando tarifas se necessário"
	)
	public ResponseEntity<ReservaResponse> cancelar(@PathVariable String codigo) {
		reservaServico.cancelarReserva(codigo);
		
		var response = new ReservaResponse(
			codigo,
			"Reserva cancelada com sucesso",
			null
		);
		
		return ResponseEntity.ok(response);
	}
}
