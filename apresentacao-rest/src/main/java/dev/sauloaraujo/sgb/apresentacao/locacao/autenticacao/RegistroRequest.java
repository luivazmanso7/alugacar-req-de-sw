package dev.sauloaraujo.sgb.apresentacao.locacao.autenticacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de cadastro de cliente.
 */
@Schema(description = "Requisição de cadastro de cliente")
public record RegistroRequest(
	
	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
	@Schema(description = "Nome completo do cliente", example = "João Silva")
	String nome,
	
	@NotBlank(message = "CPF/CNPJ é obrigatório")
	@Pattern(regexp = "\\d{11,14}", message = "CPF/CNPJ inválido")
	@Schema(description = "CPF ou CNPJ do cliente (apenas números)", example = "12345678901")
	String cpfOuCnpj,
	
	@NotBlank(message = "CNH é obrigatória")
	@Pattern(regexp = "\\d{11}", message = "CNH inválida")
	@Schema(description = "Número da CNH (11 dígitos)", example = "12345678901")
	String cnh,
	
	@NotBlank(message = "E-mail é obrigatório")
	@Email(message = "E-mail inválido")
	@Schema(description = "E-mail do cliente", example = "joao.silva@email.com")
	String email,
	
	@NotBlank(message = "Login é obrigatório")
	@Pattern(regexp = "^[a-zA-Z0-9._-]{4,30}$", 
	         message = "Login deve ter 4-30 caracteres alfanuméricos, pontos, hífens ou underscores")
	@Schema(description = "Login para acesso ao sistema", example = "joao.silva")
	String login,
	
	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
	@Schema(description = "Senha de acesso", example = "senha123")
	String senha
) {}
