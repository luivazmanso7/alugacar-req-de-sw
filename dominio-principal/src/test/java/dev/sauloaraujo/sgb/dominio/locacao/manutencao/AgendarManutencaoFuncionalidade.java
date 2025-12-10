package dev.sauloaraujo.sgb.dominio.locacao.manutencao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import dev.sauloaraujo.sgb.dominio.locacao.evento.VeiculoFoiParaManutencaoEvent;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class AgendarManutencaoFuncionalidade extends AlugacarFuncionalidade {

	private VeiculoFoiParaManutencaoEvent eventoGerado;

	@Before
	public void preparar() {
		eventoGerado = null;
	}

	@Quando("eu agendo manutencao para o veiculo {string} com previsao {string} e motivo {string}")
	public void eu_agendo_manutencao_para_o_veiculo(String placa, String previsao, String motivo) {
		try {
			eventoGerado = manutencaoServico.agendar(placa, LocalDateTime.parse(previsao), motivo);
		} catch (RuntimeException ex) {
			registrarErro(ex);
		}
	}

	@Entao("o veiculo {string} deve ficar em manutencao com nota {string}")
	public void o_veiculo_deve_ficar_em_manutencao_com_nota(String placa, String nota) {
		assertNotNull(eventoGerado);
		assertEquals(placa, eventoGerado.placa());
		assertEquals(nota, eventoGerado.motivo());

		var veiculoAgendado = repositorio.buscarPorPlaca(placa)
				.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
		assertEquals(StatusVeiculo.EM_MANUTENCAO, veiculoAgendado.getStatus());
		assertEquals(nota, veiculoAgendado.getManutencaoNota());
	}
}
