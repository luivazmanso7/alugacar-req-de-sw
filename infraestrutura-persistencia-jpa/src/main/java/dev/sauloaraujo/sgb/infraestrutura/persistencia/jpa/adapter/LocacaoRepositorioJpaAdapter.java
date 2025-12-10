package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.JpaMapeador;
import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.LocacaoJpa;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository.LocacaoJpaRepository;

/**
 * Adaptador que implementa o repositório de domínio usando Spring Data JPA.
 */
@Component
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
                .map(mapeador::paraLocacao);
    }

    @Override
    @Transactional
    public void salvar(Locacao locacao) {
        LocacaoJpa jpa = mapeador.paraLocacaoJpa(locacao);
        jpaRepository.save(jpa);
    }
}
