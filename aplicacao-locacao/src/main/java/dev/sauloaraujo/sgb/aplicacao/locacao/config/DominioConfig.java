package dev.sauloaraujo.sgb.aplicacao.locacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CatalogoVeiculosServico;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.ClienteRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.manutencao.ManutencaoServico;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.DevolucaoServico;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.RetiradaServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaCancelamentoServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaReplanejamentoServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaServico;

@Configuration
public class DominioConfig {

    @Bean
    public DevolucaoServico devolucaoServico(
            LocacaoRepositorio locacaoRepositorio,
            VeiculoRepositorio veiculoRepositorio) {
        return new DevolucaoServico(locacaoRepositorio, veiculoRepositorio);
    }

    @Bean
    public ManutencaoServico manutencaoServico(VeiculoRepositorio veiculoRepositorio) {
        return new ManutencaoServico(veiculoRepositorio);
    }

    @Bean
    public ReservaServico reservaServico(
            ReservaRepositorio reservaRepositorio,
            CategoriaRepositorio categoriaRepositorio,
            ClienteRepositorio clienteRepositorio,
            LocacaoRepositorio locacaoRepositorio,
            VeiculoRepositorio veiculoRepositorio) {
        return new ReservaServico(reservaRepositorio, categoriaRepositorio, clienteRepositorio, locacaoRepositorio, veiculoRepositorio);
    }

    @Bean
    public RetiradaServico retiradaServico(
            ReservaRepositorio reservaRepositorio,
            VeiculoRepositorio veiculoRepositorio,
            LocacaoRepositorio locacaoRepositorio) {
        return new RetiradaServico(reservaRepositorio, veiculoRepositorio, locacaoRepositorio);
    }

    @Bean
    public ReservaCancelamentoServico reservaCancelamentoServico(ReservaRepositorio reservaRepositorio) {
        return new ReservaCancelamentoServico(reservaRepositorio);
    }

    @Bean
    public ReservaReplanejamentoServico reservaReplanejamentoServico(
            ReservaRepositorio reservaRepositorio,
            CategoriaRepositorio categoriaRepositorio,
            LocacaoRepositorio locacaoRepositorio) {
        return new ReservaReplanejamentoServico(reservaRepositorio, categoriaRepositorio, locacaoRepositorio);
    }

    @Bean
    public CatalogoVeiculosServico catalogoVeiculosServico(
            CategoriaRepositorio categoriaRepositorio,
            VeiculoRepositorio veiculoRepositorio,
            LocacaoRepositorio locacaoRepositorio) {
        return new CatalogoVeiculosServico(categoriaRepositorio, veiculoRepositorio, locacaoRepositorio);
    }
}
