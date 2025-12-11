package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;

/**
 * Command de aplicação para criação de reserva.
 * 
 * @param categoriaCodigo código da categoria do veículo
 * @param cidadeRetirada cidade onde o veículo será retirado
 * @param periodo período de locação (retirada e devolução)
 * @param cliente cliente que está fazendo a reserva
 */
public record CriarReservaCmd(
        CategoriaCodigo categoriaCodigo,
        String cidadeRetirada,
        PeriodoLocacao periodo,
        Cliente cliente) {

    public CriarReservaCmd {
        Objects.requireNonNull(categoriaCodigo, "Categoria é obrigatória");
        Objects.requireNonNull(cidadeRetirada, "Cidade de retirada é obrigatória");
        if (cidadeRetirada.isBlank()) {
            throw new IllegalArgumentException("Cidade de retirada não pode estar vazia");
        }
        Objects.requireNonNull(periodo, "Período é obrigatório");
        Objects.requireNonNull(cliente, "Cliente é obrigatório");
    }
}

