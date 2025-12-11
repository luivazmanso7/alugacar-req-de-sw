package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.JpaMapeador;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities.LocacaoJpa;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository.LocacaoJpaRepository;

/**
 * Adaptador que implementa o repositório de domínio usando Spring Data JPA.
 * NOTA: Esta classe está desabilitada porque LocacaoRepositorioImpl já implementa LocacaoRepositorio.
 */
// @Component - Desabilitado para evitar conflito com LocacaoRepositorioImpl
public class LocacaoRepositorioJpaAdapter implements LocacaoRepositorio {

    private final LocacaoJpaRepository jpaRepository;
    private final JpaMapeador mapeador;

    public LocacaoRepositorioJpaAdapter(LocacaoJpaRepository jpaRepository, JpaMapeador mapeador) {
        this.jpaRepository = jpaRepository;
        this.mapeador = mapeador;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Locacao> buscarPorCodigoLocacao(String codigo) {
        return jpaRepository.findByCodigo(codigo)
                .map(jpa -> mapeador.map(jpa, Locacao.class));
    }

    @Override
    @Transactional
    public void salvar(Locacao locacao) {
        LocacaoJpa jpa = mapeador.map(locacao, LocacaoJpa.class);
        jpaRepository.save(jpa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Locacao> listarLocacoes() {
        return jpaRepository.findAll().stream()
                .map(jpa -> mapeador.map(jpa, Locacao.class))
                .collect(Collectors.toList());
    }
}
