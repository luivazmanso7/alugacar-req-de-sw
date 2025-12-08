package dev.sauloaraujo.sgb.aplicacao.locacao.auditoria;

import dev.sauloaraujo.sgb.dominio.locacao.auditoria.Auditoria;
import dev.sauloaraujo.sgb.dominio.locacao.evento.LocacaoRealizadaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o listener de auditoria de locação.
 * 
 * @since 2.0
 * @version 2.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - AuditoriaLocacaoListener")
class AuditoriaLocacaoListenerTest {
    
    @Mock
    private AuditoriaRepositorioAplicacao repositorio;
    
    @Captor
    private ArgumentCaptor<Auditoria> auditoriaCaptor;
    
    private AuditoriaLocacaoListener listener;
    
    @BeforeEach
    void setUp() {
        listener = new AuditoriaLocacaoListener(repositorio);
    }
    
    @Test
    @DisplayName("Deve criar registro de auditoria ao receber evento de locação")
    void deveCriarRegistroDeAuditoriaAoReceberEvento() {
        // Arrange
        LocacaoRealizadaEvent evento = new LocacaoRealizadaEvent(
            "LOC-001",
            "cliente@email.com",
            LocalDateTime.now()
        );
        
        when(repositorio.salvar(any(Auditoria.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        listener.onLocacaoRealizada(evento);
        
        // Assert
        verify(repositorio, times(1)).salvar(auditoriaCaptor.capture());
        
        Auditoria auditoriaSalva = auditoriaCaptor.getValue();
        assertThat(auditoriaSalva).isNotNull();
        assertThat(auditoriaSalva.getOperacao()).isEqualTo("LOCACAO_REALIZADA");
        assertThat(auditoriaSalva.getDetalhes())
            .contains("LOC-001")
            .contains("cliente@email.com");
        assertThat(auditoriaSalva.getUsuario()).isEqualTo("sistema");
    }
    
    @Test
    @DisplayName("Deve gerar detalhes completos no registro de auditoria")
    void deveGerarDetalhesCompletosNoRegistro() {
        // Arrange
        LocalDateTime dataLocacao = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocacaoRealizadaEvent evento = new LocacaoRealizadaEvent(
            "LOC-123",
            "joao@example.com",
            dataLocacao
        );
        
        when(repositorio.salvar(any(Auditoria.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        listener.onLocacaoRealizada(evento);
        
        // Assert
        verify(repositorio).salvar(auditoriaCaptor.capture());
        
        Auditoria auditoria = auditoriaCaptor.getValue();
        String detalhesEsperados = String.format(
            "Locação %s realizada para o cliente %s em %s",
            "LOC-123",
            "joao@example.com",
            dataLocacao
        );
        
        assertThat(auditoria.getDetalhes()).isEqualTo(detalhesEsperados);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao criar listener com repositório nulo")
    void deveLancarExcecaoAoCriarListenerComRepositorioNulo() {
        // Act & Assert
        assertThatThrownBy(() -> new AuditoriaLocacaoListener(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Repositório de auditoria não pode ser nulo");
    }
}
