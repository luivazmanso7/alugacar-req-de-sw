package dev.sauloaraujo.sgb.apresentacao.locacao.cliente;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.cliente.ClienteResumo;
import dev.sauloaraujo.sgb.aplicacao.locacao.cliente.ClienteServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para operações de consulta de Clientes.
 * Expõe endpoints para listar e buscar clientes.
 */
@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Operações de consulta de clientes")
public class ClienteController {
	
	private final ClienteServicoAplicacao clienteServico;

	public ClienteController(ClienteServicoAplicacao clienteServico) {
		this.clienteServico = clienteServico;
	}

	/**
	 * Lista todos os clientes.
	 * 
	 * @return Lista de clientes
	 */
	@GetMapping
	@Operation(summary = "Listar clientes", description = "Retorna todos os clientes cadastrados")
	public ResponseEntity<List<ClienteResumo>> listar() {
		var clientes = clienteServico.pesquisarResumos();
		return ResponseEntity.ok(clientes);
	}

	/**
	 * Busca um cliente específico por CPF ou CNPJ.
	 * 
	 * @param cpfOuCnpj CPF ou CNPJ do cliente
	 * @return Cliente encontrado ou 404 se não existir
	 */
	@GetMapping("/{cpfOuCnpj}")
	@Operation(summary = "Buscar cliente por CPF/CNPJ", description = "Retorna um cliente específico pelo CPF ou CNPJ")
	public ResponseEntity<ClienteResumo> buscarPorCpfOuCnpj(@PathVariable String cpfOuCnpj) {
		return clienteServico.buscarPorCpfOuCnpj(cpfOuCnpj)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
