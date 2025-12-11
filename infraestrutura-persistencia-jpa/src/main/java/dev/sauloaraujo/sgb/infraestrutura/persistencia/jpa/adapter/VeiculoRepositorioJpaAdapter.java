package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.JpaMapeador;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.VeiculoJpa;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository.VeiculoJpaRepository;


@Component
public class VeiculoRepositorioJpaAdapter implements VeiculoRepositorio {

    private final VeiculoJpaRepository jpaRepository;
    private final JpaMapeador mapeador;

    public VeiculoRepositorioJpaAdapter(VeiculoJpaRepository jpaRepository, JpaMapeador mapeador) {
        this.jpaRepository = jpaRepository;
        this.mapeador = mapeador;
    }

    @Override
    @Transactional
    public void salvar(Veiculo veiculo) {
        var veiculoJpa = mapeador.map(veiculo, VeiculoJpa.class);
        jpaRepository.save(veiculoJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Veiculo> buscarPorPlaca(String placa) {
        return jpaRepository.findById(placa)
                .map(jpa -> mapeador.map(jpa, Veiculo.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Veiculo> buscarDisponiveis(String cidade, CategoriaCodigo categoria) {
        var veiculosJpa = jpaRepository.findDisponiveisPorCidadeECategoria(cidade, categoria);
        return mapeador.mapList(veiculosJpa, Veiculo.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Veiculo> buscarDisponiveis(String cidade) {
        var veiculosJpa = jpaRepository.findDisponiveisPorCidade(cidade);
        return mapeador.mapList(veiculosJpa, Veiculo.class);
    }
}
