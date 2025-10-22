package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class ConsultarReservaFuncionalidade extends AlugacarFuncionalidade {
	private InformacaoReserva informacaoReserva;
	private List<StatusReserva> status;

	@Quando("o cliente acessa informações sobre reservas")
	public void o_cliente_acessa_informacoes_sobre_reservas() {
		informacaoReserva = reservaServico.obterInformacoesReserva();
	}

	@Entao("deve ver que reservas têm códigos únicos")
	public void deve_ver_que_reservas_tem_codigos_unicos() {
		assertTrue(informacaoReserva.possuiCodigoUnico());
	}

	@Entao("deve ver que reservas são para categorias de veículos")
	public void deve_ver_que_reservas_sao_para_categorias_de_veiculos() {
		assertTrue(informacaoReserva.baseadaEmCategoria());
	}

	@Entao("deve ver que reservas têm períodos definidos")
	public void deve_ver_que_reservas_tem_periodos_definidos() {
		assertTrue(informacaoReserva.possuiPeriodoDefinido());
	}

	@Entao("deve ver que reservas têm valores estimados")
	public void deve_ver_que_reservas_tem_valores_estimados() {
		assertTrue(informacaoReserva.possuiValorEstimado());
	}

	@Quando("o cliente consulta possíveis status de reservas")
	public void o_cliente_consulta_possiveis_status_de_reservas() {
		status = reservaServico.statusDisponiveis();
	}

	@Entao("deve ver status {string} como válido")
	public void deve_ver_status_como_valido(String statusEsperado) {
		var esperado = StatusReserva.valueOf(statusEsperado);
		assertTrue(status.contains(esperado));
	}
}
