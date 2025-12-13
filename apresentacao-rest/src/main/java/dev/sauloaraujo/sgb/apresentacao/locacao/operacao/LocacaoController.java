package dev.sauloaraujo.sgb.apresentacao.locacao.operacao;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.LocacaoResumo;
import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.LocacaoServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para operações de consulta e gerenciamento de Locações.
 * Expõe endpoints para listar, buscar e processar devoluções de locações.
 */
@RestController
@RequestMapping("/locacoes")
@Tag(name = "Locações", description = "Operações de consulta e gerenciamento de locações de veículos")
public class LocacaoController {
	
	private final LocacaoServicoAplicacao locacaoServico;

	public LocacaoController(LocacaoServicoAplicacao locacaoServico) {
		this.locacaoServico = locacaoServico;
	}

	/**
	 * Lista todas as locações.
	 * 
	 * @return Lista de locações
	 */
	@GetMapping
	@Operation(summary = "Listar locações", description = "Retorna todas as locações cadastradas")
	public ResponseEntity<List<LocacaoResumo>> listar() {
		var locacoes = locacaoServico.pesquisarResumos();
		return ResponseEntity.ok(locacoes);
	}

	/**
	 * Busca uma locação específica por código.
	 * 
	 * @param codigo Código da locação
	 * @return Locação encontrada ou 404 se não existir
	 */
	@GetMapping("/{codigo}")
	@Operation(summary = "Buscar locação por código", description = "Retorna uma locação específica pelo código")
	public ResponseEntity<LocacaoResumo> buscarPorCodigo(@PathVariable String codigo) {
		return locacaoServico.buscarPorCodigo(codigo)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Lista apenas as locações ativas.
	 * 
	 * @return Lista de locações ativas
	 */
	@GetMapping("/ativas")
	@Operation(summary = "Listar locações ativas", description = "Retorna todas as locações com status ativo")
	public ResponseEntity<List<LocacaoResumo>> listarAtivas() {
		var locacoes = locacaoServico.listarAtivas();
		return ResponseEntity.ok(locacoes);
	}

	/**
	 * Lista locações de um cliente específico.
	 * 
	 * @param cpfOuCnpj CPF ou CNPJ do cliente
	 * @return Lista de locações do cliente
	 */
	@GetMapping("/cliente/{cpfOuCnpj}")
	@Operation(summary = "Listar locações por cliente", 
	           description = "Retorna todas as locações de um cliente específico")
	public ResponseEntity<List<LocacaoResumo>> listarPorCliente(
			@Parameter(description = "CPF ou CNPJ do cliente") @PathVariable String cpfOuCnpj) {
		var locacoes = locacaoServico.listarPorCliente(cpfOuCnpj);
		return ResponseEntity.ok(locacoes);
	}
}
