package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Faturamento;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ProcessarDevolucaoCommand;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class ProcessarDevolucaoFuncionalidade extends AlugacarFuncionalidade {
    private Faturamento faturamento;
    private RuntimeException erro;
    private Reserva reservaProcessada;
    private Locacao locacaoProcessada;

    @Before
    public void preparar() {
        faturamento = null;
        erro = null;
        reservaProcessada = null;
        locacaoProcessada = null;
    }

    @Quando("eu processo a devolucao da reserva {string} na data {string} com odometro {int} e combustivel {int} sem danos")
    public void processo_devolucao_sem_danos(String codigoReserva, String dataDevolucao, Integer odometro,
            Integer combustivel) {
        processarDevolucao(codigoReserva, dataDevolucao, odometro, combustivel, false, BigDecimal.ZERO);
    }

    @Quando(
            "eu processo a devolucao da reserva {string} na data {string} com odometro {int} e combustivel {int} com danos e taxa adicional {double}")
    public void processo_devolucao_com_danos(String codigoReserva, String dataDevolucao, Integer odometro,
            Integer combustivel, Double taxa) {
        processarDevolucao(codigoReserva, dataDevolucao, odometro, combustivel, true, BigDecimal.valueOf(taxa));
    }

    @Entao("o valor final da locacao deve ser igual ao valor estimado da reserva")
    public void valor_final_igual_ao_estimado() {
        assertNotNull(faturamento);
        assertNotNull(reservaProcessada);
        assertEquals(reservaProcessada.getValorEstimado(), faturamento.total());
    }

    @Entao("o veiculo {string} fica disponivel para nova locacao")
    public void veiculo_disponivel_para_nova_locacao(String placa) {
        var veiculo = repositorio.buscarPorPlaca(placa)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        assertTrue(veiculo.getStatus().disponivel());
    }

    @Entao("o valor final da locacao deve ser maior que o valor estimado da reserva")
    public void valor_final_maior_que_estimado() {
        assertNotNull(faturamento);
        assertNotNull(reservaProcessada);
        assertTrue(faturamento.total().compareTo(reservaProcessada.getValorEstimado()) > 0);
    }

	@Entao("o veiculo {string} deve ser enviado para manutencao")
	public void veiculo_enviado_para_manutencao(String placa) {
		var veiculo = repositorio.buscarPorPlaca(placa)
				.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
		assertEquals(StatusVeiculo.EM_MANUTENCAO, veiculo.getStatus());
	}

	@Entao("o veiculo {string} retorna ao patio da cidade {string}")
	public void veiculo_retorna_ao_patio(String placa, String cidade) {
		var veiculo = repositorio.buscarPorPlaca(placa)
				.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
		assertNotNull(veiculo.getPatio());
		assertEquals(cidade, veiculo.getPatio().getCidade());
	}

	private void processarDevolucao(String codigoReserva, String dataDevolucao, Integer odometro, Integer combustivel,
	        boolean possuiAvarias, BigDecimal taxaAdicional) {
	    var locacao = repositorio.buscarPorCodigoLocacao("LOCACAO-" + codigoReserva)
	            .orElseThrow(() -> new IllegalArgumentException("Locação não encontrada"));

        reservaProcessada = locacao.getReserva();
        locacaoProcessada = locacao;

        var dataEntrega = LocalDateTime.parse(dataDevolucao);

        var builder = ProcessarDevolucaoCommand.builder()
                .codigoLocacao(locacao.getCodigo())
                .quilometragem(odometro)
                .combustivel(combustivel.toString())
                .dataDevolucao(dataEntrega)
                .taxaCombustivel(taxaAdicional)
                .possuiAvarias(possuiAvarias);

        // Se houver atraso, usar percentual maior
        var diasAtraso = Math.max(0,
                (int) Duration.between(reservaProcessada.getPeriodo().getDevolucao(), dataEntrega).toDays());
        if (diasAtraso > 0) {
            builder.percentualMultaAtraso(new BigDecimal("0.30"));
        }

        try {
            faturamento = devolucaoServico.processar(builder.build());
        } catch (RuntimeException ex) {
            erro = ex;
        }
    }
}
