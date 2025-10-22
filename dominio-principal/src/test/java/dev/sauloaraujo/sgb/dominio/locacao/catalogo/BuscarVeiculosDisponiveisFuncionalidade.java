package dev.sauloaraujo.sgb.dominio.locacao.catalogo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;

public class BuscarVeiculosDisponiveisFuncionalidade extends AlugacarFuncionalidade {
	private PeriodoLocacao periodo;
	private CategoriaCodigo filtroCategoria;
	private List<VeiculoDisponivel> veiculosEncontrados;
	private final String cidade = "São Paulo";

	@Dado("que existem veículos cadastrados no sistema")
	public void que_existem_veiculos_cadastrados_no_sistema() {
		catalogoServico.registrarCategoria(new Categoria(CategoriaCodigo.ECONOMICO, "Econômico",
				"Modelos compactos para o dia a dia", new BigDecimal("50.00"), List.of("HB20", "Onix"), 10));
		catalogoServico.registrarCategoria(new Categoria(CategoriaCodigo.SUV, "SUV", "Modelos espaçosos",
				new BigDecimal("120.00"), List.of("Compass", "Kicks"), 5));

		catalogoServico.registrarVeiculo(new Veiculo("ABC-1234", "HB20", CategoriaCodigo.ECONOMICO, cidade,
				new BigDecimal("50.00"), StatusVeiculo.DISPONIVEL));
		catalogoServico.registrarVeiculo(new Veiculo("XYZ-9876", "Compass", CategoriaCodigo.SUV, cidade,
				new BigDecimal("120.00"), StatusVeiculo.DISPONIVEL));
	}

	@E("os veículos estão disponíveis")
	public void os_veiculos_estao_disponiveis() {
		// cenário já configurado na etapa anterior
	}

	@Dado("que o cliente quer buscar veículos")
	public void que_o_cliente_quer_buscar_veiculos() {
		// sem configuração adicional
	}

	@E("informa data de retirada {string}")
	public void informa_data_de_retirada(String inicio) {
		var retirada = LocalDateTime.parse(inicio);
		var fim = periodo != null ? periodo.getDevolucao() : retirada.plusDays(3);
		periodo = new PeriodoLocacao(retirada, fim);
	}

	@E("informa data de devolução {string}")
	public void informa_data_de_devolucao(String fim) {
		var devolucao = LocalDateTime.parse(fim);
		var retirada = periodo != null ? periodo.getRetirada() : devolucao.minusDays(3);
		periodo = new PeriodoLocacao(retirada, devolucao);
	}

	@Quando("o cliente busca veículos disponíveis")
	public void o_cliente_busca_veiculos_disponiveis() {
		var consultaBuilder = ConsultaDisponibilidade.builder().cidade(cidade).periodo(periodo);
		if (filtroCategoria != null) {
			consultaBuilder.categoria(filtroCategoria);
		}
		veiculosEncontrados = catalogoServico.buscarDisponiveis(consultaBuilder.construir());
	}

	@Entao("deve ver a lista de veículos disponíveis")
	public void deve_ver_a_lista_de_veiculos_disponiveis() {
		assertNotNull(veiculosEncontrados);
		assertFalse(veiculosEncontrados.isEmpty());
	}

	@E("cada veículo deve ter placa, modelo, categoria e valor da diária")
	public void cada_veiculo_deve_ter_placa_modelo_categoria_e_valor_da_diaria() {
		assertTrue(veiculosEncontrados.stream().allMatch(veiculo -> veiculo.placa() != null && veiculo.modelo() != null
				&& veiculo.categoria() != null && veiculo.diaria() != null));
	}

	@E("seleciona categoria {string}")
	public void seleciona_categoria(String categoria) {
		filtroCategoria = CategoriaCodigo.fromTexto(categoria);
	}

	@Entao("deve ver apenas veículos da categoria {string}")
	public void deve_ver_apenas_veiculos_da_categoria(String categoria) {
		var esperado = CategoriaCodigo.fromTexto(categoria);
		assertTrue(veiculosEncontrados.stream().allMatch(veiculo -> veiculo.categoria() == esperado));
	}
}
