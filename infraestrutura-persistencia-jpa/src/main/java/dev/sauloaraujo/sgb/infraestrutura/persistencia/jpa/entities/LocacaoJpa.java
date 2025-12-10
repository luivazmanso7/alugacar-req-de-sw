package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.LocacaoRepositorioAplicacao;
import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.LocacaoResumo;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusLocacao;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidade JPA para persistência de Locações.
 */
@Entity
@Table(name = "LOCACAO")
class LocacaoJpa {

    @Id
    @Column(name = "codigo", nullable = false, length = 50)
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "reserva_codigo", nullable = false)
    private ReservaJpa reserva;

    @ManyToOne
    @JoinColumn(name = "veiculo_placa", nullable = false)
    private VeiculoJpa veiculo;

    @Column(name = "dias_previstos", nullable = false)
    private int diasPrevistos;

    @Column(name = "valor_diaria", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDiaria;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusLocacao status;

    @Embedded
    private ChecklistVistoriaJpa vistoriaRetirada;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "quilometragem", column = @Column(name = "vistoria_devolucao_km")),
            @AttributeOverride(name = "combustivel", column = @Column(name = "vistoria_devolucao_combustivel")),
            @AttributeOverride(name = "possuiAvarias", column = @Column(name = "vistoria_devolucao_avarias"))
    })
    private ChecklistVistoriaJpa vistoriaDevolucao;

    LocacaoJpa() {
    }

    // Getters e Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public ReservaJpa getReserva() { return reserva; }
    public void setReserva(ReservaJpa reserva) { this.reserva = reserva; }
    public VeiculoJpa getVeiculo() { return veiculo; }
    public void setVeiculo(VeiculoJpa veiculo) { this.veiculo = veiculo; }
    public int getDiasPrevistos() { return diasPrevistos; }
    public void setDiasPrevistos(int diasPrevistos) { this.diasPrevistos = diasPrevistos; }
    public BigDecimal getValorDiaria() { return valorDiaria; }
    public void setValorDiaria(BigDecimal valorDiaria) { this.valorDiaria = valorDiaria; }
    public StatusLocacao getStatus() { return status; }
    public void setStatus(StatusLocacao status) { this.status = status; }
    public ChecklistVistoriaJpa getVistoriaRetirada() { return vistoriaRetirada; }
    public void setVistoriaRetirada(ChecklistVistoriaJpa vistoriaRetirada) { this.vistoriaRetirada = vistoriaRetirada; }
    public ChecklistVistoriaJpa getVistoriaDevolucao() { return vistoriaDevolucao; }
    public void setVistoriaDevolucao(ChecklistVistoriaJpa vistoriaDevolucao) { this.vistoriaDevolucao = vistoriaDevolucao; }
}

/**
 * Interface Spring Data JPA.
 * Adicionados métodos de busca para suportar as queries da aplicação.
 */
interface LocacaoJpaRepository extends JpaRepository<LocacaoJpa, String> {
    
    // Busca para listarAtivas()
    List<LocacaoJpa> findByStatus(StatusLocacao status);
    
    // Busca para listarPorCliente() - Navega pelas propriedades: Reserva -> Cliente -> CpfOuCnpj
    // O nome do campo na entidade ClienteJpa é 'cpfOuCnpj' (verifique se na sua entidade ClienteJpa é este o nome exato do atributo ID)
    // Se o ID em ClienteJpa for 'cpfOuCnpj', o caminho é findByReserva_Cliente_CpfOuCnpj
    List<LocacaoJpa> findByReserva_Cliente_CpfOuCnpj(String cpfOuCnpj);
}

/**
 * Implementação Unificada dos Repositórios.
 * CORREÇÃO: Implementa tanto a interface de Domínio quanto a de Aplicação.
 */
@Repository
class LocacaoRepositorioImpl implements LocacaoRepositorio, LocacaoRepositorioAplicacao {

    @Autowired
    LocacaoJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    // ========================================================================
    // Implementação de LocacaoRepositorio (Domínio - Escrita/Regras)
    // ========================================================================

    @Override
    public void salvar(Locacao locacao) {
        var locacaoJpa = mapeador.map(locacao, LocacaoJpa.class);
        repositorio.save(locacaoJpa);
    }

    @Override
    public Optional<Locacao> buscarPorCodigoLocacao(String codigo) {
        return repositorio.findById(codigo)
                .map(jpa -> mapeador.map(jpa, Locacao.class));
    }

    @Override
    public List<Locacao> listarLocacoes() {
        var locacoesJpa = repositorio.findAll();
        return mapeador.mapList(locacoesJpa, Locacao.class);
    }

    // ========================================================================
    // Implementação de LocacaoRepositorioAplicacao (Aplicação - Leitura/DTOs)
    // ========================================================================

    @Override
    public List<LocacaoResumo> pesquisarResumos() {
        return repositorio.findAll().stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LocacaoResumo> buscarPorCodigo(String codigo) {
        return repositorio.findById(codigo)
                .map(this::converterParaResumo);
    }

    @Override
    public List<LocacaoResumo> listarAtivas() {
        return repositorio.findByStatus(StatusLocacao.ATIVA).stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocacaoResumo> listarPorCliente(String cpfOuCnpj) {
        return repositorio.findByReserva_Cliente_CpfOuCnpj(cpfOuCnpj).stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Helper para converter a Entidade JPA diretamente para a Projeção (DTO) de Resumo.
     * Implementa a interface LocacaoResumo anonimamente.
     */
    private LocacaoResumo converterParaResumo(LocacaoJpa jpa) {
        return new LocacaoResumo() {
            @Override public String getCodigo() { return jpa.getCodigo(); }
            @Override public String getReservaCodigo() { return jpa.getReserva().getCodigo(); }
            @Override public String getVeiculoPlaca() { return jpa.getVeiculo().getPlaca(); }
            @Override public String getVeiculoModelo() { return jpa.getVeiculo().getModelo(); }
            @Override public String getClienteNome() { return jpa.getReserva().getCliente().getNome(); }
            @Override public int getDiasPrevistos() { return jpa.getDiasPrevistos(); }
            @Override public BigDecimal getValorDiaria() { return jpa.getValorDiaria(); }
            @Override public String getStatus() { return jpa.getStatus().name(); }
        };
    }
}