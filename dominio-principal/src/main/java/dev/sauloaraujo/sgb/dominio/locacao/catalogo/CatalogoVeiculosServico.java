package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import java.util.List;
import java.util.Objects;

public class CatalogoVeiculosServico {
	private final CategoriaRepositorio categoriaRepositorio;
	private final VeiculoRepositorio veiculoRepositorio;

	public CatalogoVeiculosServico(CategoriaRepositorio categoriaRepositorio, VeiculoRepositorio veiculoRepositorio) {
		this.categoriaRepositorio = Objects.requireNonNull(categoriaRepositorio, "Repositorio de categorias é obrigatório");
		this.veiculoRepositorio = Objects.requireNonNull(veiculoRepositorio, "Repositorio de veículos é obrigatório");
	}

	public void registrarCategoria(Categoria categoria) {
		categoriaRepositorio.salvar(categoria);
	}

	public void registrarVeiculo(Veiculo veiculo) {
		veiculoRepositorio.salvar(veiculo);
	}

	public List<VeiculoDisponivel> buscarDisponiveis(ConsultaDisponibilidade consulta) {
		Objects.requireNonNull(consulta, "A consulta é obrigatória");

		var veiculos = consulta.getCategoria()
				.map(categoria -> veiculoRepositorio.buscarDisponiveis(consulta.getCidade(), categoria))
				.orElseGet(() -> veiculoRepositorio.buscarDisponiveis(consulta.getCidade()));

		return veiculos.stream()
				.map(veiculo -> new VeiculoDisponivel(veiculo.getPlaca(), veiculo.getModelo(), veiculo.getCategoria(),
						veiculo.getDiaria()))
				.toList();
	}

	public List<Categoria> listarCategorias() {
		return categoriaRepositorio.listarTodas();
	}

	public Categoria detalharCategoria(CategoriaCodigo codigo) {
		return categoriaRepositorio.buscarPorCodigo(codigo)
				.orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada: " + codigo));
	}
}
