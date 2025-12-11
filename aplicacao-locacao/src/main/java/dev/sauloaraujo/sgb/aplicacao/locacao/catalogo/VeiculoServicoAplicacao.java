package dev.sauloaraujo.sgb.aplicacao.locacao.catalogo;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;

/**
 * Serviço de aplicação para operações de consulta de veículos.
 */
@Service
public class VeiculoServicoAplicacao {

    private final VeiculoRepositorio veiculoRepositorio;

    public VeiculoServicoAplicacao(VeiculoRepositorio veiculoRepositorio) {
        this.veiculoRepositorio = notNull(veiculoRepositorio, 
            "Repositório de veículos não pode ser nulo");
    }

    @Transactional(readOnly = true)
    public Optional<VeiculoResumo> buscarPorPlaca(String placa) {
        return veiculoRepositorio.buscarPorPlaca(placa)
                .map(this::toResumo);
    }

    @Transactional(readOnly = true)
    public List<VeiculoResumo> buscarDisponiveis(String cidade, CategoriaCodigo categoria) {
        var veiculos = veiculoRepositorio.buscarDisponiveis(cidade, categoria);
        return veiculos.stream()
                .map(this::toResumo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VeiculoResumo> buscarDisponiveis(String cidade) {
        var veiculos = veiculoRepositorio.buscarDisponiveis(cidade);
        return veiculos.stream()
                .map(this::toResumo)
                .collect(Collectors.toList());
    }

    private VeiculoResumo toResumo(Veiculo veiculo) {
        return new VeiculoResumo(
                veiculo.getPlaca(),
                veiculo.getModelo(),
                veiculo.getCategoria().name(),
                veiculo.getCidade(),
                veiculo.getDiaria(),
                veiculo.getStatus().name()
        );
    }
}

