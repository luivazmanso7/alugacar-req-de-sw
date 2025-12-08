package dev.sauloaraujo.sgb.aplicacao.locacao.auditoria;

import dev.sauloaraujo.sgb.dominio.locacao.auditoria.Auditoria;
import dev.sauloaraujo.sgb.dominio.locacao.evento.LocacaoRealizadaEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Listener que audita eventos de locação.
 * 
 * <p>
 * Implementa o padrão Observer através do sistema de eventos do Spring.
 * Quando uma locação é realizada, este listener captura o evento e
 * persiste um registro de auditoria no banco de dados.
 * </p>
 * 
 * <p><strong>Padrões aplicados:</strong></p>
 * <ul>
 *   <li>Observer Pattern - reage a eventos de domínio</li>
 *   <li>Event Sourcing - persiste histórico de eventos</li>
 * </ul>
 * 
 * @since 2.0
 * @version 2.0
 */
@Component
public class AuditoriaLocacaoListener {
    
    private final AuditoriaRepositorioAplicacao repositorio;
    
    /**
     * Construtor com injeção de dependências.
     * 
     * @param repositorio repositório de auditoria
     */
    public AuditoriaLocacaoListener(AuditoriaRepositorioAplicacao repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, 
            "Repositório de auditoria não pode ser nulo");
    }
    
    /**
     * Escuta eventos de locação realizada e cria registro de auditoria.
     * 
     * @param evento evento contendo dados da locação
     */
    @EventListener
    public void onLocacaoRealizada(LocacaoRealizadaEvent evento) {
        String detalhes = String.format(
            "Locação %s realizada para o cliente %s em %s",
            evento.codigoLocacao(),
            evento.emailCliente(),
            evento.dataLocacao()
        );
        
        Auditoria auditoria = new Auditoria(
            "LOCACAO_REALIZADA",
            detalhes,
            "sistema" // Em produção, seria o usuário autenticado
        );
        
        repositorio.salvar(auditoria);
        
        System.out.println("AUDITORIA REGISTRADA: " + auditoria);
    }
}
