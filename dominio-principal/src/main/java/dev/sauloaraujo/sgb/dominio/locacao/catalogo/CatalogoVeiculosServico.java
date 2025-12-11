package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusLocacao;

public class CatalogoVeiculosServico {
	private final CategoriaRepositorio categoriaRepositorio;
	private final VeiculoRepositorio veiculoRepositorio;
	private final LocacaoRepositorio locacaoRepositorio;

	public CatalogoVeiculosServico(CategoriaRepositorio categoriaRepositorio, VeiculoRepositorio veiculoRepositorio,
			LocacaoRepositorio locacaoRepositorio) {
		this.categoriaRepositorio = Objects.requireNonNull(categoriaRepositorio, "Repositorio de categorias é obrigatório");
		this.veiculoRepositorio = Objects.requireNonNull(veiculoRepositorio, "Repositorio de veículos é obrigatório");
		this.locacaoRepositorio = Objects.requireNonNull(locacaoRepositorio, "Repositorio de locações é obrigatório");
	}

	public void registrarCategoria(Categoria categoria) {
		categoriaRepositorio.salvar(categoria);
	}

	public void registrarVeiculo(Veiculo veiculo) {
		veiculoRepositorio.salvar(veiculo);
	}

	/**
	 * Busca veículos disponíveis para um período específico.
	 * REGRA DE NEGÓCIO: Exclui veículos que estão locados durante o período solicitado,
	 * mesmo que o status seja DISPONIVEL.
	 */
	public List<VeiculoDisponivel> buscarDisponiveis(ConsultaDisponibilidade consulta) {
		Objects.requireNonNull(consulta, "A consulta é obrigatória");

		// 1. Buscar veículos com status DISPONIVEL
		var veiculos = consulta.getCategoria()
				.map(categoria -> veiculoRepositorio.buscarDisponiveis(consulta.getCidade(), categoria))
				.orElseGet(() -> veiculoRepositorio.buscarDisponiveis(consulta.getCidade()));

		// 2. Obter placas de veículos locados no período solicitado
		Set<String> placasLocadasNoPeriodo = obterPlacasLocadasNoPeriodo(consulta.getPeriodo());

		// 3. Filtrar veículos que não estão locados no período
		return veiculos.stream()
				.filter(veiculo -> !placasLocadasNoPeriodo.contains(veiculo.getPlaca()))
				.map(veiculo -> new VeiculoDisponivel(veiculo.getPlaca(), veiculo.getModelo(), veiculo.getCategoria(),
						veiculo.getDiaria()))
				.toList();
	}

	/**
	 * Obtém as placas dos veículos que estão locados (status ATIVA) durante o período solicitado.
	 */
	private Set<String> obterPlacasLocadasNoPeriodo(PeriodoLocacao periodo) {
		return locacaoRepositorio.listarLocacoes().stream()
				.filter(locacao -> locacao.getStatus() == StatusLocacao.ATIVA)
				.filter(locacao -> periodosConflitantes(locacao.getReserva().getPeriodo(), periodo))
				.map(locacao -> locacao.getVeiculo().getPlaca())
				.collect(Collectors.toSet());
	}

	/**
	 * Verifica se dois períodos conflitam (se há sobreposição).
	 */
	private boolean periodosConflitantes(PeriodoLocacao existente, PeriodoLocacao desejado) {
		LocalDateTime inicioExistente = existente.getRetirada();
		LocalDateTime fimExistente = existente.getDevolucao();
		LocalDateTime inicioNovo = desejado.getRetirada();
		LocalDateTime fimNovo = desejado.getDevolucao();
		return !inicioNovo.isAfter(fimExistente) && !fimNovo.isBefore(inicioExistente);
	}

	public List<Categoria> listarCategorias() {
		return categoriaRepositorio.listarTodas();
	}

	public Categoria detalharCategoria(CategoriaCodigo codigo) {
		return categoriaRepositorio.buscarPorCodigo(codigo)
				.orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada: " + codigo));
	}
}
