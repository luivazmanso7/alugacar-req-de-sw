package dev.sauloaraujo.sgb.apresentacao.locacao.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para resposta de autenticação.
 */
@Schema(description = "Resposta de autenticação")
public record AutenticacaoResponse(
	
	@Schema(description = "CPF ou CNPJ do cliente", example = "12345678901")
	String cpfOuCnpj,
	
	@Schema(description = "Nome do cliente", example = "João Silva")
	String nome,
	
	@Schema(description = "E-mail do cliente", example = "joao.silva@email.com")
	String email,
	
	@Schema(description = "Login do cliente", example = "joao.silva")
	String login,
	
	@Schema(description = "Status do cliente", example = "ATIVO")
	String status,
	
	@Schema(description = "Mensagem de sucesso", example = "Autenticação realizada com sucesso")
	String mensagem
) {}
