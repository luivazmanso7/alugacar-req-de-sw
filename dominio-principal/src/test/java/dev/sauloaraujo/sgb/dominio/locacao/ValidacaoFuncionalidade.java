package dev.sauloaraujo.sgb.dominio.locacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.cucumber.java.pt.Entao;

public class ValidacaoFuncionalidade extends AlugacarFuncionalidade {

	@Entao("ocorre um erro de negocio com a mensagem {string}")
	public void ocorre_um_erro_de_negocio_com_a_mensagem(String mensagem) {
		var erro = recuperarErro();
		assertNotNull(erro, "Nenhum erro foi registrado");
		assertEquals(mensagem, erro.getMessage());
	}
}
