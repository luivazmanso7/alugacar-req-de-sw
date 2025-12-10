package dev.sauloaraujo.sgb.aplicacao.locacao.operacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.sauloaraujo.sgb.dominio.locacao.operacao.DevolucaoServico;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Faturamento;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ProcessarDevolucaoCommand;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - Locação Serviço Aplicação")
class LocacaoServicoAplicacaoTest {

    @Mock
    private LocacaoRepositorioAplicacao repositorioLeitura;

    @Mock
    private DevolucaoServico devolucaoServico;

    private LocacaoServicoAplicacao servico;

    @BeforeEach
    void setUp() {
        // Agora injetamos o Serviço de Domínio, conforme a refatoração
        servico = new LocacaoServicoAplicacao(repositorioLeitura, devolucaoServico);
    }

    @Test
    @DisplayName("Deve delegar o processamento da devolução para o serviço de domínio")
    void deveDelegarProcessamentoParaDominio() {
        // Arrange
        var comando = ProcessarDevolucaoCommand.builder()
                .codigoLocacao("LOC-123")
                .quilometragem(10000)
                .combustivel("CHEIO")
                .build();

        var faturamentoEsperado = new Faturamento(
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );

        
        when(devolucaoServico.processar(any(ProcessarDevolucaoCommand.class)))
                .thenReturn(faturamentoEsperado);

        // Act
        var resultado = servico.processarDevolucao(comando);

        // Assert
        assertThat(resultado).isEqualTo(faturamentoEsperado);
        verify(devolucaoServico).processar(comando); 
    }
}