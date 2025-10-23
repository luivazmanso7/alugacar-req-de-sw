package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class ConfirmarRetiradaFuncionalidade extends AlugacarFuncionalidade {

	private ContratoLocacao contratoGerado;

	@Before
	public void preparar() {
		contratoGerado = null;
	}

	@Quando("eu confirmo a retirada da reserva {string} com o veiculo {string}")
	public void eu_confirmo_a_retirada_da_reserva_com_o_veiculo(String codigoReserva, String placa,
			io.cucumber.datatable.DataTable dados) {
		var registro = dados.asMaps().get(0);
		var combustivel = registro.getOrDefault("combustivel", "CHEIO");
		var quilometragem = Integer.parseInt(registro.getOrDefault("odometro", "0"));

		var commandBuilder = ProcessarRetiradaCommand.builder().codigoReserva(codigoReserva)
				.codigoLocacao("LOC-" + codigoReserva).placaVeiculo(placa).combustivel(combustivel)
				.quilometragem(quilometragem);

		if (registro.containsKey("documentosValidos")) {
			var documentosValidos = Boolean.parseBoolean(registro.get("documentosValidos"));
			commandBuilder.documentosValidos(documentosValidos);
		}

		try {
			contratoGerado = retiradaServico.processar(commandBuilder.build());
		} catch (RuntimeException ex) {
			registrarErro(ex);
		}
	}

	@Entao("o contrato de locacao e criado para o veiculo {string}")
	public void o_contrato_de_locacao_e_criado_para_o_veiculo(String placa) {
		assertNotNull(contratoGerado);
		assertEquals(placa, contratoGerado.placaVeiculo());
	}

	@Entao("a reserva {string} passa a ter o status {string}")
	public void a_reserva_passa_a_ter_o_status(String codigoReserva, String statusEsperado) {
		var reserva = repositorio.buscarPorCodigo(codigoReserva)
				.orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
		assertEquals(statusEsperado, reserva.getStatus().name());
	}
}
