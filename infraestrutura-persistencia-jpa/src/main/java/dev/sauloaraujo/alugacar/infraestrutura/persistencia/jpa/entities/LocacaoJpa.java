package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public ReservaJpa getReserva() {
		return reserva;
	}

	public void setReserva(ReservaJpa reserva) {
		this.reserva = reserva;
	}

	public VeiculoJpa getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(VeiculoJpa veiculo) {
		this.veiculo = veiculo;
	}

	public int getDiasPrevistos() {
		return diasPrevistos;
	}

	public void setDiasPrevistos(int diasPrevistos) {
		this.diasPrevistos = diasPrevistos;
	}

	public BigDecimal getValorDiaria() {
		return valorDiaria;
	}

	public void setValorDiaria(BigDecimal valorDiaria) {
		this.valorDiaria = valorDiaria;
	}

	public StatusLocacao getStatus() {
		return status;
	}

	public void setStatus(StatusLocacao status) {
		this.status = status;
	}

	public ChecklistVistoriaJpa getVistoriaRetirada() {
		return vistoriaRetirada;
	}

	public void setVistoriaRetirada(ChecklistVistoriaJpa vistoriaRetirada) {
		this.vistoriaRetirada = vistoriaRetirada;
	}

	public ChecklistVistoriaJpa getVistoriaDevolucao() {
		return vistoriaDevolucao;
	}

	public void setVistoriaDevolucao(ChecklistVistoriaJpa vistoriaDevolucao) {
		this.vistoriaDevolucao = vistoriaDevolucao;
	}
}

interface LocacaoJpaRepository extends JpaRepository<LocacaoJpa, String> {
}

@Repository
class LocacaoRepositorioImpl implements LocacaoRepositorio {

	@Autowired
	LocacaoJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

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
}
