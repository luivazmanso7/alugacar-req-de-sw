package dev.sauloaraujo.sgb.apresentacao.locacao.reserva;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para criação de reserva.
 */
@Schema(description = "Requisição para criar uma nova reserva")
public record CriarReservaRequest(
	
	@NotBlank(message = "CPF do cliente é obrigatório")
	@Schema(description = "CPF ou CNPJ do cliente", example = "12345678901")
	String cpfCliente,
	
	@NotBlank(message = "Categoria é obrigatória")
	@Schema(description = "Código da categoria do veículo", example = "ECONOMICO")
	String categoria,
	
	@NotBlank(message = "Cidade de retirada é obrigatória")
	@Schema(description = "Cidade onde o veículo será retirado", example = "São Paulo")
	String cidadeRetirada,
	
	@NotNull(message = "Data de retirada é obrigatória")
	@Future(message = "Data de retirada deve ser futura")
	@Schema(description = "Data e hora previstas para retirada", example = "2026-12-15T10:00:00")
	LocalDateTime dataRetirada,
	
	@NotNull(message = "Data de devolução é obrigatória")
	@Future(message = "Data de devolução deve ser futura")
	@Schema(description = "Data e hora previstas para devolução", example = "2026-12-20T10:00:00")
	LocalDateTime dataDevolucao
) {}
