package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.JpaMapeador;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository.ReservaJpaRepository;
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
 * Entidade JPA para persistência de Reservas.
 */
@Entity
@Table(name = "RESERVA")
public class ReservaJpa {

	@Id
	@Column(name = "codigo", nullable = false, length = 50)
	private String codigo;

	@Column(name = "categoria", nullable = false, length = 20)
	private String categoria;

	@Column(name = "cidade_retirada", nullable = false, length = 100)
	private String cidadeRetirada;

	@Embedded
	private PeriodoLocacaoJpa periodo;

	@Column(name = "valor_estimado", nullable = false, precision = 10, scale = 2)
	private BigDecimal valorEstimado;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private StatusReserva status;

	@ManyToOne
	@JoinColumn(name = "cliente_cpf_cnpj", nullable = false)
	private ClienteJpa cliente;

	public ReservaJpa() {
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getCidadeRetirada() {
		return cidadeRetirada;
	}

	public void setCidadeRetirada(String cidadeRetirada) {
		this.cidadeRetirada = cidadeRetirada;
	}

	public PeriodoLocacaoJpa getPeriodo() {
		return periodo;
	}

	public void setPeriodo(PeriodoLocacaoJpa periodo) {
		this.periodo = periodo;
	}

	public BigDecimal getValorEstimado() {
		return valorEstimado;
	}

	public void setValorEstimado(BigDecimal valorEstimado) {
		this.valorEstimado = valorEstimado;
	}

	public StatusReserva getStatus() {
		return status;
	}

	public void setStatus(StatusReserva status) {
		this.status = status;
	}

	public ClienteJpa getCliente() {
		return cliente;
	}

	public void setCliente(ClienteJpa cliente) {
		this.cliente = cliente;
	}
}

@Repository("reservaRepositorioReal")
class ReservaRepositorioImpl implements ReservaRepositorio {

	@Autowired
	ReservaJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Reserva reserva) {
		var reservaJpa = mapeador.map(reserva, ReservaJpa.class);
		repositorio.save(reservaJpa);
	}

	@Override
	public Optional<Reserva> buscarPorCodigo(String codigo) {
		System.out.println("[REAL] Buscando no banco...");
		return repositorio.findById(codigo)
				.map(jpa -> mapeador.map(jpa, Reserva.class));
	}

	@Override
	public List<Reserva> listar() {
		var reservasJpa = repositorio.findAll();
		return mapeador.mapList(reservasJpa, Reserva.class);
	}
}
