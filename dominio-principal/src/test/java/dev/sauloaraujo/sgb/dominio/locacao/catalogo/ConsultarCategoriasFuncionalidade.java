package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;

public class ConsultarCategoriasFuncionalidade extends AlugacarFuncionalidade {
	private List<Categoria> categorias;
	private Categoria categoriaDetalhada;
	private RuntimeException erro;

	private static final Map<CategoriaCodigo, Categoria> CATEGORIAS_PADRAO = Map.of(
			CategoriaCodigo.ECONOMICO,
			new Categoria(CategoriaCodigo.ECONOMICO, "Econômico", "Veículos compactos ideais para economia",
					new BigDecimal("50.00"), List.of("HB20", "Onix"), 8),
			CategoriaCodigo.INTERMEDIARIO,
			new Categoria(CategoriaCodigo.INTERMEDIARIO, "Intermediário", "Veículos com mais conforto",
					new BigDecimal("70.00"), List.of("Corolla", "Civic"), 6),
			CategoriaCodigo.EXECUTIVO,
			new Categoria(CategoriaCodigo.EXECUTIVO, "Executivo", "Modelos premium para negócios",
					new BigDecimal("120.00"), List.of("BMW 320i", "Audi A4"), 3),
			CategoriaCodigo.PREMIUM,
			new Categoria(CategoriaCodigo.PREMIUM, "Premium", "Veículos de alto padrão",
					new BigDecimal("200.00"), List.of("Mercedes C200", "Volvo XC60"), 2),
			CategoriaCodigo.SUV, new Categoria(CategoriaCodigo.SUV, "SUV", "Espaço e versatilidade para família",
					new BigDecimal("150.00"), List.of("Compass", "Tiguan"), 5));

	@Dado("que existem categorias de veículos cadastradas")
	public void que_existem_categorias_de_veiculos_cadastradas() {
		CATEGORIAS_PADRAO.values().forEach(catalogoServico::registrarCategoria);
	}

	@Quando("o cliente consulta as categorias")
	public void o_cliente_consulta_as_categorias() {
		categorias = catalogoServico.listarCategorias();
	}

	@Entao("deve ver a lista de todas as categorias")
	public void deve_ver_a_lista_de_todas_as_categorias() {
		assertNotNull(categorias);
		assertEquals(CATEGORIAS_PADRAO.size(), categorias.size());
	}

	@E("cada categoria deve ter nome, descrição, valor da diária e exemplos de modelos")
	public void cada_categoria_deve_ter_nome_descricao_valor_da_diaria_e_exemplos_de_modelos() {
		assertTrue(categorias.stream().allMatch(categoria -> categoria.getNome() != null
				&& categoria.getDescricao() != null && categoria.getDiaria() != null
				&& !categoria.getModelosExemplo().isEmpty()));
	}

	@Quando("o cliente consulta detalhes da categoria {string}")
	public void o_cliente_consulta_detalhes_da_categoria(String categoria) {
		try {
			categoriaDetalhada = catalogoServico.detalharCategoria(CategoriaCodigo.fromTexto(categoria));
			erro = null;
		} catch (RuntimeException ex) {
			erro = ex;
		}
	}

	@Entao("deve ver o nome {string}")
	public void deve_ver_o_nome(String nomeEsperado) {
		assertNotNull(categoriaDetalhada);
		assertEquals(nomeEsperado, categoriaDetalhada.getNome());
	}

	@E("deve ver a descrição da categoria")
	public void deve_ver_a_descricao_da_categoria() {
		assertNotNull(categoriaDetalhada.getDescricao());
	}

	@E("deve ver o valor da diária")
	public void deve_ver_o_valor_da_diaria() {
		assertNotNull(categoriaDetalhada.getDiaria());
	}

	@E("deve ver lista de exemplos de modelos")
	public void deve_ver_lista_de_exemplos_de_modelos() {
		assertNotNull(categoriaDetalhada.getModelosExemplo());
		assertTrue(!categoriaDetalhada.getModelosExemplo().isEmpty());
	}

	@E("deve ver quantidade de veículos disponíveis")
	public void deve_ver_quantidade_de_veiculos_disponiveis() {
		assertTrue(categoriaDetalhada.getQuantidadeDisponivel() >= 0);
	}

	@Entao("deve ver informações específicas da categoria")
	public void deve_ver_informacoes_especificas_da_categoria() {
		assertNotNull(categoriaDetalhada);
		var categoriaEsperada = CATEGORIAS_PADRAO.get(categoriaDetalhada.getCodigo());
		if (categoriaEsperada == null) {
			fail("Categoria não configurada para validação");
		}
		assertEquals(categoriaEsperada.getNome(), categoriaDetalhada.getNome());
		assertEquals(categoriaEsperada.getDescricao(), categoriaDetalhada.getDescricao());
	}

	@Entao("deve receber erro de categoria não encontrada")
	public void deve_receber_erro_de_categoria_nao_encontrada() {
		assertNotNull(erro);
	}
}
