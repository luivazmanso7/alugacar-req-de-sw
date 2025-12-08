package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import dev.sauloaraujo.sgb.aplicacao.locacao.auditoria.AuditoriaRepositorioAplicacao;
import dev.sauloaraujo.sgb.dominio.locacao.auditoria.Auditoria;

/**
 * Teste de integração para AuditoriaRepositorio.
 * Valida o mapeamento ORM de entidade imutável e operações CRUD.
 */
@DataJpaTest
@Import({JpaMapeador.class, AuditoriaRepositorioImpl.class})
class AuditoriaRepositorioIntegrationTest {

    @Autowired
    private AuditoriaRepositorioAplicacao repositorio;

    @Test
    void deveSalvarERecuperarAuditoria() {
        // Arrange
        var auditoria = new Auditoria(
            "LOCACAO_REALIZADA",
            "Locação LOC-001 realizada para cliente@email.com",
            "sistema"
        );

        // Act
        var auditoriaSalva = repositorio.salvar(auditoria);
        var auditorias = repositorio.buscarTodas();

        // Assert
        assertThat(auditoriaSalva).isNotNull();
        assertThat(auditoriaSalva.getId()).isNotBlank();
        assertThat(auditorias).isNotEmpty();
        assertThat(auditorias.get(0).getOperacao()).isEqualTo("LOCACAO_REALIZADA");
    }

    @Test
    void deveBuscarAuditoriasPorOperacao() {
        // Arrange
        repositorio.salvar(new Auditoria("LOCACAO_REALIZADA", "Detalhe 1", "sistema"));
        repositorio.salvar(new Auditoria("LOCACAO_REALIZADA", "Detalhe 2", "sistema"));
        repositorio.salvar(new Auditoria("RESERVA_CRIADA", "Detalhe 3", "sistema"));

        // Act
        var auditorias = repositorio.buscarPorOperacao("LOCACAO_REALIZADA");

        // Assert
        assertThat(auditorias).hasSize(2);
        assertThat(auditorias)
            .allMatch(a -> a.getOperacao().equals("LOCACAO_REALIZADA"));
    }

    @Test
    void deveBuscarAuditoriasPorPeriodo() throws InterruptedException {
        // Arrange
        LocalDateTime inicio = LocalDateTime.now().minusMinutes(1);
        
        repositorio.salvar(new Auditoria("OP1", "Detalhe 1", "sistema"));
        Thread.sleep(100);
        repositorio.salvar(new Auditoria("OP2", "Detalhe 2", "sistema"));
        
        LocalDateTime fim = LocalDateTime.now().plusMinutes(1);

        // Act
        var auditorias = repositorio.buscarPorPeriodo(inicio, fim);

        // Assert
        assertThat(auditorias).hasSize(2);
    }

    @Test
    void deveManterImutabilidadeDaEntidadeDeDominio() {
        // Arrange
        var auditoria = new Auditoria(
            "TESTE_IMUTABILIDADE",
            "Teste de imutabilidade",
            "sistema"
        );

        var idOriginal = auditoria.getId();
        var dataOriginal = auditoria.getDataHora();

        // Act
        var auditoriaSalva = repositorio.salvar(auditoria);
        var auditoriaRecuperada = repositorio.buscarPorOperacao("TESTE_IMUTABILIDADE").get(0);

        // Assert - Dados originais não foram alterados
        assertThat(auditoria.getId()).isEqualTo(idOriginal);
        assertThat(auditoria.getDataHora()).isEqualTo(dataOriginal);
        
        // Assert - Persistência funcionou corretamente
        assertThat(auditoriaRecuperada.getId()).isEqualTo(idOriginal);
        assertThat(auditoriaRecuperada.getOperacao()).isEqualTo("TESTE_IMUTABILIDADE");
        assertThat(auditoriaRecuperada.getUsuario()).isEqualTo("sistema");
    }
}
