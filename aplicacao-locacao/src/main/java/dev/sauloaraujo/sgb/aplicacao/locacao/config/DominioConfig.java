package dev.sauloaraujo.sgb.aplicacao.locacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.manutencao.ManutencaoServico;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.DevolucaoServico;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;

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
}
