package dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao;

/**
 * DTO de resposta de login.
 */
public record LoginResponse(
        String clienteNome,
        String clienteDocumento,
        String clienteEmail) {
}

