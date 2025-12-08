package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.aplicacao.locacao.auditoria.AuditoriaRepositorioAplicacao;
import dev.sauloaraujo.sgb.dominio.locacao.auditoria.Auditoria;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA para persistência de auditoria.
 * Package-private conforme padrão de ocultamento da infraestrutura.
 */
@Entity
@Table(name = "auditoria")
class AuditoriaJpa {
    
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;
    
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
    
    @Column(name = "operacao", length = 100, nullable = false)
    private String operacao;
    
    @Column(name = "detalhes", length = 500)
    private String detalhes;
    
    @Column(name = "usuario", length = 100)
    private String usuario;
    
    AuditoriaJpa() {
        // JPA only
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    
    public String getOperacao() {
        return operacao;
    }
    
    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }
    
    public String getDetalhes() {
        return detalhes;
    }
    
    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }
    
    public String getUsuario() {
        return usuario;
    }
    
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}

/**
 * Repositório Spring Data JPA para auditoria.
 * Package-private conforme padrão de ocultamento da infraestrutura.
 */
interface AuditoriaJpaRepository extends JpaRepository<AuditoriaJpa, String> {
    
    List<AuditoriaJpa> findByOperacao(String operacao);
    
    List<AuditoriaJpa> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    
    List<AuditoriaJpa> findAllByOrderByDataHoraDesc();
}

/**
 * Implementação do repositório de auditoria.
 * Classe pública que expõe funcionalidades para a camada de aplicação.
 */
@Repository
class AuditoriaRepositorioImpl implements AuditoriaRepositorioAplicacao {
    
    @Autowired
    private AuditoriaJpaRepository repositorio;
    
    @Autowired
    private JpaMapeador mapeador;
    
    @Override
    public Auditoria salvar(Auditoria auditoria) {
        var auditoriaJpa = mapeador.map(auditoria, AuditoriaJpa.class);
        var salva = repositorio.save(auditoriaJpa);
        return mapeador.map(salva, Auditoria.class);
    }
    
    @Override
    public List<Auditoria> buscarPorOperacao(String operacao) {
        return repositorio.findByOperacao(operacao)
            .stream()
            .map(jpa -> mapeador.map(jpa, Auditoria.class))
            .toList();
    }
    
    @Override
    public List<Auditoria> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return repositorio.findByDataHoraBetween(inicio, fim)
            .stream()
            .map(jpa -> mapeador.map(jpa, Auditoria.class))
            .toList();
    }
    
    @Override
    public List<Auditoria> buscarTodas() {
        return repositorio.findAllByOrderByDataHoraDesc()
            .stream()
            .map(jpa -> mapeador.map(jpa, Auditoria.class))
            .toList();
    }
}
