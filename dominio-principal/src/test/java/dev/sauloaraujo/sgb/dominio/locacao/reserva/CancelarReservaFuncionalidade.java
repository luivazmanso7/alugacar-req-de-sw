package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class CancelarReservaFuncionalidade extends AlugacarFuncionalidade {

	private ReservaCancelamentoServico.ResultadoCancelamento resultado;

	@Before
	public void preparar() {
		resultado = null;
	}

	@Quando("eu solicito o cancelamento da reserva {string} em {string}")
	public void eu_solicito_o_cancelamento_da_reserva_em(String codigoReserva, String data) {
		try {
			// Buscar a reserva para obter o CPF do cliente (regra de negócio: só o dono pode cancelar)
			var reservaOpt = repositorio.buscarPorCodigo(codigoReserva);
			if (reservaOpt.isEmpty()) {
				registrarErro(new IllegalArgumentException("Reserva não encontrada"));
				return;
			}
			var cpfCliente = reservaOpt.get().getCliente().getCpfOuCnpj();
			resultado = reservaCancelamentoServico.cancelar(codigoReserva, cpfCliente, LocalDateTime.parse(data));
		} catch (RuntimeException ex) {
			registrarErro(ex);
		}
	}

	@Entao("o cancelamento e realizado com tarifa {double}")
	public void o_cancelamento_e_realizado_com_tarifa(Double tarifaEsperada) {
		assertNotNull(resultado);
		assertEquals(BigDecimal.valueOf(tarifaEsperada).stripTrailingZeros(),
				resultado.tarifa().stripTrailingZeros());
	}
}
