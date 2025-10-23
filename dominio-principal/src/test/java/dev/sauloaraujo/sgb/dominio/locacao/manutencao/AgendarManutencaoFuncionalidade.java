package dev.sauloaraujo.sgb.dominio.locacao.manutencao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class AgendarManutencaoFuncionalidade extends AlugacarFuncionalidade {

	private Veiculo veiculoAgendado;

	@Before
	public void preparar() {
		veiculoAgendado = null;
	}

	@Quando("eu agendo manutencao para o veiculo {string} com previsao {string} e motivo {string}")
	public void eu_agendo_manutencao_para_o_veiculo(String placa, String previsao, String motivo) {
		try {
			veiculoAgendado = manutencaoServico.agendar(placa, LocalDateTime.parse(previsao), motivo);
		} catch (RuntimeException ex) {
			registrarErro(ex);
		}
	}

	@Entao("o veiculo {string} deve ficar em manutencao com nota {string}")
	public void o_veiculo_deve_ficar_em_manutencao_com_nota(String placa, String nota) {
		assertNotNull(veiculoAgendado);
		assertEquals(placa, veiculoAgendado.getPlaca());
		assertEquals(StatusVeiculo.EM_MANUTENCAO, veiculoAgendado.getStatus());
		assertEquals(nota, veiculoAgendado.getManutencaoNota());
	}
}
