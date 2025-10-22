package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;

public class ProcessarRetiradaFuncionalidade extends AlugacarFuncionalidade {
	private Reserva reservaAtual;
	private ProcessarRetiradaCommand.Builder comandoBuilder;
	private ContratoLocacao contrato;
	private RuntimeException erro;
	private String placaSelecionada;

	@Dado("que existem reservas ativas no sistema")
	public void que_existem_reservas_ativas_no_sistema() {
		catalogoServico.registrarCategoria(new Categoria(CategoriaCodigo.ECONOMICO, "Econômico",
				"Veículos econômicos para cidade", new BigDecimal("50.00"), List.of("HB20", "Onix"), 10));

		var periodo = new PeriodoLocacao(LocalDateTime.parse("2025-12-01T10:00:00"),
				LocalDateTime.parse("2025-12-05T10:00:00"));
		var reserva1 = new Reserva("RSV-001", CategoriaCodigo.ECONOMICO, "São Paulo", periodo,
				new BigDecimal("200.00"), StatusReserva.ATIVA);
		reserva1.ajustarValorEstimado(new BigDecimal("50.00"));
		repositorio.salvar(reserva1);

		var periodo2 = new PeriodoLocacao(LocalDateTime.parse("2025-12-10T10:00:00"),
				LocalDateTime.parse("2025-12-12T10:00:00"));
		var reserva2 = new Reserva("RSV-002", CategoriaCodigo.ECONOMICO, "São Paulo", periodo2,
				new BigDecimal("120.00"), StatusReserva.ATIVA);
		reserva2.ajustarValorEstimado(new BigDecimal("60.00"));
		repositorio.salvar(reserva2);
	}

	@E("existem veículos disponíveis para retirada")
	public void existem_veiculos_disponiveis_para_retirada() {
		var veiculo = new Veiculo("ABC-1234", "HB20", CategoriaCodigo.ECONOMICO, "São Paulo", new BigDecimal("50.00"),
				StatusVeiculo.DISPONIVEL);
		var veiculo2 = new Veiculo("ABC-5678", "Onix", CategoriaCodigo.ECONOMICO, "São Paulo",
				new BigDecimal("50.00"), StatusVeiculo.DISPONIVEL);
		catalogoServico.registrarVeiculo(veiculo);
		catalogoServico.registrarVeiculo(veiculo2);
	}

	@Dado("que existe uma reserva ativa com código {string}")
	public void que_existe_uma_reserva_ativa_com_codigo(String codigo) {
		reservaAtual = repositorio.buscarPorCodigo(codigo).orElseThrow();
		comandoBuilder = ProcessarRetiradaCommand.builder().codigoReserva(codigo)
				.codigoLocacao("LOC-" + UUID.randomUUID().toString().substring(0, 6));
		contrato = null;
		erro = null;
	}

	@E("o cliente apresenta documentos válidos")
	public void o_cliente_apresenta_documentos_validos() {
		comandoBuilder.documentosValidos(true);
	}

	@E("existe veículo da categoria disponível")
	public void existe_veiculo_da_categoria_disponivel() {
		var veiculo = repositorio.buscarDisponiveis("São Paulo", reservaAtual.getCategoria()).stream().findFirst()
				.orElseThrow();
		placaSelecionada = veiculo.getPlaca();
		comandoBuilder.placaVeiculo(placaSelecionada);
	}

	@E("o cliente está presente para vistoria")
	public void o_cliente_esta_presente_para_vistoria() {
		// nenhuma configuração adicional necessária
	}

	@Quando("o atendente processa a retirada da reserva {string}")
	public void o_atendente_processa_a_retirada_da_reserva(String codigo) {
		comandoBuilder.codigoReserva(codigo);
	}

	@Quando("o atendente tenta processar a retirada da reserva {string}")
	public void o_atendente_tenta_processar_a_retirada_da_reserva(String codigo) {
		comandoBuilder.codigoReserva(codigo);
		executarProcesso();
	}

	@E("registra vistoria de retirada com quilometragem {string} e combustível {string}")
	public void registra_vistoria_de_retirada_com_quilometragem_e_combustivel(String quilometragem,
			String combustivel) {
		comandoBuilder.quilometragem(Integer.parseInt(quilometragem));
		comandoBuilder.combustivel(combustivel);
	}

	@E("associa o veículo {string} à locação")
	public void associa_o_veiculo_a_locacao(String placa) {
		comandoBuilder.placaVeiculo(placa);
		executarProcesso();
	}

	@Entao("a locação deve ser criada com sucesso")
	public void a_locacao_deve_ser_criada_com_sucesso() {
		assertNotNull(contrato);
	}

	@E("o veículo deve ter status {string}")
	public void o_veiculo_deve_ter_status(String status) {
		var veiculo = repositorio.buscarPorPlaca(contrato.placaVeiculo()).orElseThrow();
		assertEquals(StatusVeiculo.valueOf(status), veiculo.getStatus());
	}

	@E("a reserva deve ter status {string}")
	public void a_reserva_deve_ter_status(String status) {
		var reserva = repositorio.buscarPorCodigo(reservaAtual.getCodigo()).orElseThrow();
		assertEquals(StatusReserva.valueOf(status), reserva.getStatus());
	}

	@E("deve gerar contrato de locação")
	public void deve_gerar_contrato_de_locacao() {
		assertNotNull(contrato);
	}

	@E("o cliente apresenta CNH vencida")
	public void o_cliente_apresenta_cnh_vencida() {
		comandoBuilder.documentosValidos(false);
	}

	@Entao("a retirada deve ser rejeitada")
	public void a_devolucao_deve_ser_rejeitada() {
		assertNotNull(erro);
	}

	@E("deve exibir mensagem {string}")
	public void deve_exibir_mensagem(String mensagem) {
		assertNotNull(erro);
		assertEquals(mensagem, erro.getMessage());
	}

	@E("a reserva deve permanecer {string}")
	public void a_reserva_deve_permanecer(String status) {
		var reserva = repositorio.buscarPorCodigo(reservaAtual.getCodigo()).orElseThrow();
		assertEquals(StatusReserva.valueOf(status), reserva.getStatus());
	}

	@E("nenhum veículo deve ser associado")
	public void nenhum_veiculo_deve_ser_associado() {
		assertNull(contrato);
		if (placaSelecionada != null) {
			var veiculo = repositorio.buscarPorPlaca(placaSelecionada).orElseThrow();
			assertTrue(veiculo.getStatus().disponivel());
		}
	}

	private void executarProcesso() {
		if (contrato != null || erro != null) {
			return;
		}

		if (placaSelecionada == null) {
			repositorio.buscarDisponiveis("São Paulo").stream().findFirst().ifPresent(veiculo -> {
				placaSelecionada = veiculo.getPlaca();
				comandoBuilder.placaVeiculo(placaSelecionada);
			});
		}

		try {
			contrato = retiradaServico.processar(comandoBuilder.build());
		} catch (RuntimeException ex) {
			erro = ex;
		}
	}
}
