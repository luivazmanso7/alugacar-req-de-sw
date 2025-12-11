package dev.sauloaraujo.sgb.aplicacao.locacao.catalogo;

import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CatalogoVeiculosServico;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.ConsultaDisponibilidade;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoDisponivel;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;

/**
 * Serviço de aplicação para operações de consulta de veículos.
 */
@Service
public class VeiculoServicoAplicacao {

    private final VeiculoRepositorio veiculoRepositorio;
    private final CatalogoVeiculosServico catalogoVeiculosServico;

    public VeiculoServicoAplicacao(
            VeiculoRepositorio veiculoRepositorio,
            CatalogoVeiculosServico catalogoVeiculosServico) {
        this.veiculoRepositorio = notNull(veiculoRepositorio, 
            "Repositório de veículos não pode ser nulo");
        this.catalogoVeiculosServico = notNull(catalogoVeiculosServico,
            "Serviço de catálogo de veículos não pode ser nulo");
    }

    @Transactional(readOnly = true)
    public Optional<VeiculoResumo> buscarPorPlaca(String placa) {
        return veiculoRepositorio.buscarPorPlaca(placa)
                .map(this::toResumo);
    }

    /**
     * Busca veículos disponíveis considerando período.
     * REGRA DE NEGÓCIO: Exclui veículos locados durante o período solicitado.
     */
    @Transactional(readOnly = true)
    public List<VeiculoResumo> buscarDisponiveis(
            String cidade, 
            CategoriaCodigo categoria,
            LocalDateTime dataRetirada,
            LocalDateTime dataDevolucao) {
        
        var periodo = new PeriodoLocacao(dataRetirada, dataDevolucao);
        var consulta = ConsultaDisponibilidade.builder()
                .cidade(cidade)
                .periodo(periodo)
                .categoria(categoria)
                .construir();
        
        var veiculosDisponiveis = catalogoVeiculosServico.buscarDisponiveis(consulta);
        
        return veiculosDisponiveis.stream()
                .map(this::toResumo)
                .collect(Collectors.toList());
    }

    /**
     * Busca veículos disponíveis considerando período (sem categoria).
     * REGRA DE NEGÓCIO: Exclui veículos locados durante o período solicitado.
     */
    @Transactional(readOnly = true)
    public List<VeiculoResumo> buscarDisponiveis(
            String cidade,
            LocalDateTime dataRetirada,
            LocalDateTime dataDevolucao) {
        
        var periodo = new PeriodoLocacao(dataRetirada, dataDevolucao);
        var consulta = ConsultaDisponibilidade.builder()
                .cidade(cidade)
                .periodo(periodo)
                .construir();
        
        var veiculosDisponiveis = catalogoVeiculosServico.buscarDisponiveis(consulta);
        
        return veiculosDisponiveis.stream()
                .map(this::toResumo)
                .collect(Collectors.toList());
    }

    /**
     * Busca veículos disponíveis sem período (método de compatibilidade).
     * ATENÇÃO: Este método não filtra veículos locados, apenas verifica status.
     */
    @Transactional(readOnly = true)
    public List<VeiculoResumo> buscarDisponiveis(String cidade, CategoriaCodigo categoria) {
        var veiculos = veiculoRepositorio.buscarDisponiveis(cidade, categoria);
        return veiculos.stream()
                .map(this::toResumo)
                .collect(Collectors.toList());
    }

    /**
     * Busca veículos disponíveis sem período (método de compatibilidade).
     * ATENÇÃO: Este método não filtra veículos locados, apenas verifica status.
     */
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

    private VeiculoResumo toResumo(VeiculoDisponivel veiculoDisponivel) {
        // Buscar veículo completo para obter cidade e status
        var veiculo = veiculoRepositorio.buscarPorPlaca(veiculoDisponivel.placa())
                .orElseThrow(() -> new IllegalStateException("Veículo não encontrado: " + veiculoDisponivel.placa()));
        
        return new VeiculoResumo(
                veiculoDisponivel.placa(),
                veiculoDisponivel.modelo(),
                veiculoDisponivel.categoria().name(),
                veiculo.getCidade(),
                veiculoDisponivel.diaria(),
                veiculo.getStatus().name()
        );
    }
}

