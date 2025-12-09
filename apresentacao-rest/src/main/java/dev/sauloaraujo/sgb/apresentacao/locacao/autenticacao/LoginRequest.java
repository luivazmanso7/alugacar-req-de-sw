package dev.sauloaraujo.sgb.apresentacao.locacao.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de login.
 */
@Schema(description = "Requisição de login")
public record LoginRequest(
	
	@NotBlank(message = "Login é obrigatório")
	@Schema(description = "Login do cliente", example = "joao.silva")
	String login,
	
	@NotBlank(message = "Senha é obrigatória")
	@Schema(description = "Senha do cliente", example = "senha123")
	String senha
) {}
