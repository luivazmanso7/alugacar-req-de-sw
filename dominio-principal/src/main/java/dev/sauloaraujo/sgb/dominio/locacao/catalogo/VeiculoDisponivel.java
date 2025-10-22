package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import java.math.BigDecimal;

public record VeiculoDisponivel(String placa, String modelo, CategoriaCodigo categoria, BigDecimal diaria) {
}
