package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;

/**
 * Teste de integração para VeiculoRepositorio.
 * Valida o mapeamento ORM e operações CRUD.
 */
@DataJpaTest
@Import({JpaConfiguration.class, 
         dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.impl.VeiculoRepositorioImpl.class})
class VeiculoRepositorioIntegrationTest {

    @Autowired
    private VeiculoRepositorio veiculoRepositorio;

    @Test
    void deveSalvarERecuperarVeiculo() {
        // Arrange
        var veiculo = new Veiculo(
            "XYZ9999",
            "Fiat Uno",
            CategoriaCodigo.ECONOMICO,
            "Curitiba",
            new BigDecimal("79.90"),
            StatusVeiculo.DISPONIVEL
        );

        // Act
        veiculoRepositorio.salvar(veiculo);
        var veiculoRecuperado = veiculoRepositorio.buscarPorPlaca("XYZ9999");

        // Assert
        assertThat(veiculoRecuperado).isPresent();
        assertThat(veiculoRecuperado.get().getModelo()).isEqualTo("Fiat Uno");
        assertThat(veiculoRecuperado.get().getCidade()).isEqualTo("Curitiba");
    }

    @Test
    void deveBuscarVeiculosDisponiveisPorCidadeECategoria() {
        // Arrange
        var veiculo1 = new Veiculo("ABC0001", "Gol 1.0", CategoriaCodigo.ECONOMICO, 
            "São Paulo", new BigDecimal("89.90"), StatusVeiculo.DISPONIVEL);
        var veiculo2 = new Veiculo("ABC0002", "Onix 1.0", CategoriaCodigo.ECONOMICO, 
            "São Paulo", new BigDecimal("89.90"), StatusVeiculo.DISPONIVEL);
        var veiculo3 = new Veiculo("ABC0003", "Civic 2.0", CategoriaCodigo.COMPACTO, 
            "São Paulo", new BigDecimal("119.90"), StatusVeiculo.DISPONIVEL);

        veiculoRepositorio.salvar(veiculo1);
        veiculoRepositorio.salvar(veiculo2);
        veiculoRepositorio.salvar(veiculo3);

        // Act
        var veiculosEconomicos = veiculoRepositorio.buscarDisponiveis(
            "São Paulo", 
            CategoriaCodigo.ECONOMICO
        );

        // Assert
        assertThat(veiculosEconomicos).hasSize(2);
        assertThat(veiculosEconomicos)
            .extracting(Veiculo::getCategoria)
            .containsOnly(CategoriaCodigo.ECONOMICO);
    }

    @Test
    void deveBuscarVeiculosDisponiveisPorCidade() {
        // Arrange
        var veiculo1 = new Veiculo("RIO0001", "Gol", CategoriaCodigo.ECONOMICO, 
            "Rio de Janeiro", new BigDecimal("89.90"), StatusVeiculo.DISPONIVEL);
        var veiculo2 = new Veiculo("RIO0002", "Civic", CategoriaCodigo.COMPACTO, 
            "Rio de Janeiro", new BigDecimal("119.90"), StatusVeiculo.DISPONIVEL);
        var veiculo3 = new Veiculo("SP0001", "Onix", CategoriaCodigo.ECONOMICO, 
            "São Paulo", new BigDecimal("89.90"), StatusVeiculo.DISPONIVEL);

        veiculoRepositorio.salvar(veiculo1);
        veiculoRepositorio.salvar(veiculo2);
        veiculoRepositorio.salvar(veiculo3);

        // Act
        var veiculosRio = veiculoRepositorio.buscarDisponiveis("Rio de Janeiro");

        // Assert
        assertThat(veiculosRio).hasSize(2);
        assertThat(veiculosRio)
            .extracting(Veiculo::getCidade)
            .containsOnly("Rio de Janeiro");
    }
}
