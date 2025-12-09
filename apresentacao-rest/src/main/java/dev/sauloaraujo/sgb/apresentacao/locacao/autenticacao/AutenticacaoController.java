package dev.sauloaraujo.sgb.apresentacao.locacao.autenticacao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao.AutenticacaoException;
import dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao.AutenticacaoServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para autenticação de clientes.
 * Expõe endpoints para login e cadastro.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Operações de autenticação e cadastro de clientes")
public class AutenticacaoController {
	
	private final AutenticacaoServicoAplicacao autenticacaoServico;

	public AutenticacaoController(AutenticacaoServicoAplicacao autenticacaoServico) {
		this.autenticacaoServico = autenticacaoServico;
	}

	/**
	 * Autentica um cliente no sistema.
	 * 
	 * @param request Dados de login (login e senha)
	 * @return Token de autenticação ou erro 401
	 */
	@PostMapping("/login")
	@Operation(
		summary = "Autenticar cliente", 
		description = "Autentica um cliente com login e senha, retornando dados do cliente autenticado"
	)
	public ResponseEntity<AutenticacaoResponse> login(@Valid @RequestBody LoginRequest request) {
		try {
			var cliente = autenticacaoServico.autenticar(request.login(), request.senha());
			
			var response = new AutenticacaoResponse(
				cliente.getCpfOuCnpj(),
				cliente.getNome(),
				cliente.getEmail(),
				cliente.getCredenciais().getLogin(),
				cliente.getStatus().name(),
				"Autenticação realizada com sucesso"
			);
			
			return ResponseEntity.ok(response);
			
		} catch (AutenticacaoException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	/**
	 * Cadastra um novo cliente no sistema.
	 * 
	 * @param request Dados do cliente
	 * @return Cliente cadastrado ou erro 400
	 */
	@PostMapping("/registro")
	@Operation(
		summary = "Cadastrar cliente", 
		description = "Cadastra um novo cliente no sistema com dados pessoais e credenciais de acesso"
	)
	public ResponseEntity<AutenticacaoResponse> registrar(@Valid @RequestBody RegistroRequest request) {
		var cliente = autenticacaoServico.cadastrarCliente(
			request.nome(),
			request.cpfOuCnpj(),
			request.cnh(),
			request.email(),
			request.login(),
			request.senha()
		);
		
		var response = new AutenticacaoResponse(
			cliente.getCpfOuCnpj(),
			cliente.getNome(),
			cliente.getEmail(),
			cliente.getCredenciais().getLogin(),
			cliente.getStatus().name(),
			"Cliente cadastrado com sucesso"
		);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
