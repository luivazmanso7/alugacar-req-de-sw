package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class AlterarPeriodoReservaFuncionalidade extends AlugacarFuncionalidade {

	private Reserva reservaReplanejada;

	@Before
	public void preparar() {
		reservaReplanejada = null;
	}

	@Quando("eu altero a reserva {string} para o periodo de {string} ate {string}")
	public void eu_altero_a_reserva_para_o_periodo(String codigoReserva, String inicio, String fim) {
		var novoPeriodo = new PeriodoLocacao(LocalDateTime.parse(inicio), LocalDateTime.parse(fim));
		try {
			reservaReplanejada = reservaReplanejamentoServico.replanejar(codigoReserva, novoPeriodo);
		} catch (RuntimeException ex) {
			registrarErro(ex);
		}
	}

	@Entao("o periodo da reserva deve ser atualizado para {string} ate {string}")
	public void o_periodo_da_reserva_deve_ser_atualizado(String inicioEsperado, String fimEsperado) {
		assertNotNull(reservaReplanejada);
		assertEquals(LocalDateTime.parse(inicioEsperado), reservaReplanejada.getPeriodo().getRetirada());
		assertEquals(LocalDateTime.parse(fimEsperado), reservaReplanejada.getPeriodo().getDevolucao());
	}

	@Entao("o valor total da reserva replanejada deve ser maior que {int}")
	public void o_valor_total_da_reserva_replanejada_deve_ser_maior_que(Integer valorMinimo) {
		assertNotNull(reservaReplanejada);
		assertTrue(reservaReplanejada.getValorEstimado().compareTo(BigDecimal.valueOf(valorMinimo)) > 0);
	}
}
