package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;

public class CriarReservaFuncionalidade extends AlugacarFuncionalidade {
	private RequisitosCriacaoReserva requisitos;
	private List<Categoria> categorias;

	@Quando("o cliente consulta como fazer uma reserva")
	public void o_cliente_consulta_como_fazer_uma_reserva() {
		requisitos = reservaServico.requisitosCriacao();
	}

	@Entao("deve ver que precisa informar dados pessoais")
	public void deve_ver_que_precisa_informar_dados_pessoais() {
		assertTrue(requisitos.exigeDadosPessoais());
	}

	@E("deve ver que precisa selecionar uma categoria")
	public void deve_ver_que_precisa_selecionar_uma_categoria() {
		assertTrue(requisitos.exigeCategoria());
	}

	@E("deve ver que precisa informar período de locação")
	public void deve_ver_que_precisa_informar_periodo_de_locacao() {
		assertTrue(requisitos.exigePeriodoLocacao());
	}

	@E("deve ver que precisa informar cidade de retirada")
	public void deve_ver_que_precisa_informar_cidade_de_retirada() {
		assertTrue(requisitos.exigeCidadeRetirada());
	}

	@Quando("o cliente consulta categorias para reserva")
	public void o_cliente_consulta_categorias_para_reserva() {
		categorias = catalogoServico.listarCategorias();
	}

	@Dado("que categorias estão disponíveis para reserva")
	public void que_categorias_estao_disponiveis_para_reserva() {
		catalogoServico.registrarCategoria(new Categoria(CategoriaCodigo.ECONOMICO, "Econômico",
				"Veículos essenciais para economia", new BigDecimal("50.00"), List.of("HB20", "Onix"), 10));
		catalogoServico.registrarCategoria(new Categoria(CategoriaCodigo.INTERMEDIARIO, "Intermediário",
				"Veículos mais confortáveis", new BigDecimal("70.00"), List.of("Corolla", "Civic"), 6));
		catalogoServico.registrarCategoria(new Categoria(CategoriaCodigo.EXECUTIVO, "Executivo",
				"Modelos premium para negócios", new BigDecimal("120.00"), List.of("BMW 320i", "Audi A4"), 3));
		catalogoServico.registrarCategoria(new Categoria(CategoriaCodigo.PREMIUM, "Premium", "Alto padrão e luxo",
				new BigDecimal("200.00"), List.of("Mercedes C200", "Volvo XC60"), 2));
		catalogoServico.registrarCategoria(new Categoria(CategoriaCodigo.SUV, "SUV", "Espaço para família",
				new BigDecimal("150.00"), List.of("Compass", "Tiguan"), 5));
	}

	@Entao("deve ver categoria {string} disponível")
	public void deve_ver_categoria_disponivel(String categoriaEsperada) {
		var codigo = CategoriaCodigo.fromTexto(categoriaEsperada);
		assertTrue(categorias.stream().anyMatch(categoria -> categoria.getCodigo() == codigo));
	}
}
