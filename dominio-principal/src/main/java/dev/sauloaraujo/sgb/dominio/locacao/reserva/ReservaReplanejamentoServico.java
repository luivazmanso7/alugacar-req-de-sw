package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;

public class ReservaReplanejamentoServico {
	private final ReservaRepositorio reservaRepositorio;
	private final CategoriaRepositorio categoriaRepositorio;
	private final LocacaoRepositorio locacaoRepositorio;

	public ReservaReplanejamentoServico(ReservaRepositorio reservaRepositorio, CategoriaRepositorio categoriaRepositorio,
			LocacaoRepositorio locacaoRepositorio) {
		this.reservaRepositorio = Objects.requireNonNull(reservaRepositorio, "Repositorio de reservas é obrigatório");
		this.categoriaRepositorio = Objects.requireNonNull(categoriaRepositorio,
				"Repositorio de categorias é obrigatório");
		this.locacaoRepositorio = Objects.requireNonNull(locacaoRepositorio, "Repositorio de locações é obrigatório");
	}

	public Reserva replanejar(String codigoReserva, PeriodoLocacao novoPeriodo) {
		var reserva = reservaRepositorio.buscarPorCodigo(codigoReserva)
				.orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));

		validarDisponibilidade(reserva, novoPeriodo);

		var categoria = categoriaRepositorio.buscarPorCodigo(reserva.getCategoria())
				.orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

		reserva.replanejar(novoPeriodo, categoria.getDiaria());
		reservaRepositorio.salvar(reserva);
		return reserva;
	}

	private void validarDisponibilidade(Reserva reserva, PeriodoLocacao novoPeriodo) {
		var categoria = reserva.getCategoria();
		var capacidade = categoriaRepositorio.buscarPorCodigo(categoria)
				.orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada")).getQuantidadeDisponivel();

		long reservasConflitantes = reservaRepositorio.listar().stream()
				.filter(outra -> !outra.getCodigo().equals(reserva.getCodigo()))
				.filter(outra -> outra.getCategoria().equals(categoria))
				.filter(outra -> outra.getStatus().ativa())
				.filter(outra -> periodosConflitantes(outra.getPeriodo(), novoPeriodo))
				.count();

		long locacoesConflitantes = locacaoRepositorio.listarLocacoes().stream()
				.filter(locacao -> locacao.getReserva().getCategoria().equals(categoria))
				.filter(locacao -> locacao.getStatus() == StatusLocacao.ATIVA)
				.filter(locacao -> periodosConflitantes(locacao.getReserva().getPeriodo(), novoPeriodo)).count();

		long ocupacao = reservasConflitantes + locacoesConflitantes + 1; // inclui a reserva replanejada
		if (ocupacao > capacidade) {
			throw new IllegalStateException("Período indisponível para a categoria desejada");
		}
	}

	private boolean periodosConflitantes(PeriodoLocacao existente, PeriodoLocacao desejado) {
		var inicioExistente = existente.getRetirada();
		var fimExistente = existente.getDevolucao();
		var inicioNovo = desejado.getRetirada();
		var fimNovo = desejado.getDevolucao();

		return !inicioNovo.isAfter(fimExistente) && !fimNovo.isBefore(inicioExistente);
	}
}
