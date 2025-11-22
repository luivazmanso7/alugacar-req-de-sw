package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;
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
class ReservaJpa {

	@Id
	@Column(name = "codigo", nullable = false, length = 50)
	String codigo;

	@Enumerated(EnumType.STRING)
	@Column(name = "categoria", nullable = false, length = 20)
	CategoriaCodigo categoria;

	@Column(name = "cidade_retirada", nullable = false, length = 100)
	String cidadeRetirada;

	@Embedded
	PeriodoLocacaoJpa periodo;

	@Column(name = "valor_estimado", nullable = false, precision = 10, scale = 2)
	BigDecimal valorEstimado;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	StatusReserva status;

	@ManyToOne
	@JoinColumn(name = "cliente_cpf_cnpj", nullable = false)
	ClienteJpa cliente;
}

/**
 * Repositório Spring Data JPA para Reserva.
 */
interface ReservaJpaRepository extends JpaRepository<ReservaJpa, String> {
}

/**
 * Implementação do repositório do domínio para Reserva.
 */
@Repository
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
		return repositorio.findById(codigo)
				.map(jpa -> mapeador.map(jpa, Reserva.class));
	}

	@Override
	public List<Reserva> listar() {
		var reservas = repositorio.findAll();
		return mapeador.mapList(reservas, Reserva.class);
	}
}
