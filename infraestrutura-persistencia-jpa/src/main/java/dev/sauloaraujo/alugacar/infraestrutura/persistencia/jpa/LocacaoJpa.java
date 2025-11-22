package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa;

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
@AttributeOverrides({
	@AttributeOverride(name = "vistoriaRetirada.quilometragem", column = @Column(name = "vistoria_retirada_km")),
	@AttributeOverride(name = "vistoriaRetirada.combustivel", column = @Column(name = "vistoria_retirada_combustivel")),
	@AttributeOverride(name = "vistoriaRetirada.possuiAvarias", column = @Column(name = "vistoria_retirada_avarias")),
	@AttributeOverride(name = "vistoriaDevolucao.quilometragem", column = @Column(name = "vistoria_devolucao_km")),
	@AttributeOverride(name = "vistoriaDevolucao.combustivel", column = @Column(name = "vistoria_devolucao_combustivel")),
	@AttributeOverride(name = "vistoriaDevolucao.possuiAvarias", column = @Column(name = "vistoria_devolucao_avarias"))
})
class LocacaoJpa {

	@Id
	@Column(name = "codigo", nullable = false, length = 50)
	String codigo;

	@ManyToOne
	@JoinColumn(name = "reserva_codigo", nullable = false)
	ReservaJpa reserva;

	@ManyToOne
	@JoinColumn(name = "veiculo_placa", nullable = false)
	VeiculoJpa veiculo;

	@Column(name = "dias_previstos", nullable = false)
	int diasPrevistos;

	@Column(name = "valor_diaria", nullable = false, precision = 10, scale = 2)
	BigDecimal valorDiaria;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	StatusLocacao status;

	@Embedded
	ChecklistVistoriaJpa vistoriaRetirada;

	@Embedded
	ChecklistVistoriaJpa vistoriaDevolucao;
}

/**
 * Repositório Spring Data JPA para Locação.
 */
interface LocacaoJpaRepository extends JpaRepository<LocacaoJpa, String> {
}

/**
 * Implementação do repositório do domínio para Locação.
 */
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
		var locacoes = repositorio.findAll();
		return mapeador.mapList(locacoes, Locacao.class);
	}
}
