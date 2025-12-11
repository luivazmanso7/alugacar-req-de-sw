package dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao;

import java.util.Objects;

/**
 * Command de aplicação para login de cliente.
 */
public record LoginCmd(
        String login,
        String senha) {

    public LoginCmd {
        Objects.requireNonNull(login, "Login é obrigatório");
        Objects.requireNonNull(senha, "Senha é obrigatória");
        if (login.isBlank()) {
            throw new IllegalArgumentException("Login não pode estar vazio");
        }
        if (senha.isBlank()) {
            throw new IllegalArgumentException("Senha não pode estar vazia");
        }
    }
}

