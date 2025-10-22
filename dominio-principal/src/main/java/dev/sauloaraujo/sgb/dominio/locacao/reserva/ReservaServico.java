package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;

public class ReservaServico {
    private static final BigDecimal FATOR_ALTA_DEMANDA = BigDecimal.valueOf(1.25);
	private final ReservaRepositorio reservaRepositorio;
	private final CategoriaRepositorio categoriaRepositorio;

	public ReservaServico(ReservaRepositorio reservaRepositorio, CategoriaRepositorio categoriaRepositorio) {
		this.reservaRepositorio = Objects.requireNonNull(reservaRepositorio, "Repositorio de reservas é obrigatório");
		this.categoriaRepositorio = Objects.requireNonNull(categoriaRepositorio,
				"Repositorio de categorias é obrigatório");
	}

	public InformacaoReserva obterInformacoesReserva() {
		return new InformacaoReserva(true, true, true, true);
	}

	public List<StatusReserva> statusDisponiveis() {
		return List.of(StatusReserva.values());
	}

	public RequisitosCriacaoReserva requisitosCriacao() {
		return new RequisitosCriacaoReserva(true, true, true, true);
	}

	public Reserva criarReserva(String codigo, CategoriaCodigo categoriaCodigo, String cidadeRetirada,
			PeriodoLocacao periodo, Cliente cliente) {
		Objects.requireNonNull(codigo, "O código é obrigatório");
		var categoria = obterCategoria(categoriaCodigo);

		validarDisponibilidade(categoriaCodigo, periodo, categoria.getQuantidadeDisponivel());

		var valorEstimado = calcularValorEstimado(categoria.getDiaria(), periodo, categoriaCodigo,
				categoria.getQuantidadeDisponivel());
		var reserva = new Reserva(codigo, categoriaCodigo, cidadeRetirada, periodo, valorEstimado, StatusReserva.ATIVA,
				cliente);
		reservaRepositorio.salvar(reserva);

		return reserva;
	}

    private void validarDisponibilidade(CategoriaCodigo categoriaCodigo, PeriodoLocacao periodo,
            int capacidadeCategoria) {
        var ocupacao = calcularOcupacao(categoriaCodigo, periodo);
        var disponibilidade = capacidadeCategoria - ocupacao;
        if (disponibilidade <= 0) {
            throw new IllegalStateException("Não há veículos disponíveis para o período selecionado");
        }
    }

	public List<Categoria> categoriasDisponiveis() {
		return categoriaRepositorio.listarTodas();
	}

    private BigDecimal calcularValorEstimado(BigDecimal valorDiaria, PeriodoLocacao periodo,
            CategoriaCodigo categoriaCodigo, int capacidadeCategoria) {
        var base = valorDiaria.multiply(BigDecimal.valueOf(periodo.dias()));
        var ocupacao = calcularOcupacao(categoriaCodigo, periodo);
        var disponibilidade = capacidadeCategoria - ocupacao;

        if (disponibilidade <= 0) {
            throw new IllegalStateException("Não há veículos disponíveis para o período selecionado");
        }

        if (disponibilidade == 1) {
            return base.multiply(FATOR_ALTA_DEMANDA);
        }

        return base;
    }

    private int calcularOcupacao(CategoriaCodigo categoriaCodigo, PeriodoLocacao periodo) {
        return Math.toIntExact(reservaRepositorio.listar().stream()
                .filter(reserva -> reserva.getCategoria().equals(categoriaCodigo))
                .filter(reserva -> reserva.getStatus().ativa())
                .filter(reserva -> periodosConflitantes(reserva.getPeriodo(), periodo)).count());
    }

    private boolean periodosConflitantes(PeriodoLocacao existente, PeriodoLocacao desejado) {
        LocalDateTime inicioExistente = existente.getRetirada();
        LocalDateTime fimExistente = existente.getDevolucao();
        LocalDateTime inicioNovo = desejado.getRetirada();
        LocalDateTime fimNovo = desejado.getDevolucao();
        return !inicioNovo.isAfter(fimExistente) && !fimNovo.isBefore(inicioExistente);
    }

	private Categoria obterCategoria(CategoriaCodigo categoria) {
		return categoriaRepositorio.buscarPorCodigo(categoria)
				.orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada: " + categoria));
	}
}
