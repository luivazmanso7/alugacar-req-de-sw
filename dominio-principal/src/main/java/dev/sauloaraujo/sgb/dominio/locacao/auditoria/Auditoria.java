package dev.sauloaraujo.sgb.dominio.locacao.auditoria;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio que representa um registro de auditoria.
 * 
 * <p>
 * Eventos do sistema são capturados e persistidos para fins de
 * rastreabilidade, compliance e análise.
 * </p>
 * 
 * @since 2.0
 * @version 2.0
 */
public class Auditoria {
    private final String id;
    private final LocalDateTime dataHora;
    private final String operacao;
    private final String detalhes;
    private final String usuario;
    
    /**
     * Cria um novo registro de auditoria.
     * 
     * @param operacao tipo da operação (ex: "LOCACAO_REALIZADA")
     * @param detalhes informações adicionais sobre a operação
     * @param usuario identificação do usuário/sistema que executou
     */
    public Auditoria(String operacao, String detalhes, String usuario) {
        this.id = UUID.randomUUID().toString();
        this.dataHora = LocalDateTime.now();
        this.operacao = Objects.requireNonNull(operacao, "Operação não pode ser nula");
        this.detalhes = detalhes;
        this.usuario = usuario;
    }
    
    /**
     * Construtor completo para reconstrução a partir do banco de dados.
     * 
     * @param id identificador único
     * @param dataHora data/hora do evento
     * @param operacao tipo da operação
     * @param detalhes informações adicionais
     * @param usuario identificação do usuário
     */
    public Auditoria(String id, LocalDateTime dataHora, String operacao, 
                     String detalhes, String usuario) {
        this.id = Objects.requireNonNull(id, "ID não pode ser nulo");
        this.dataHora = Objects.requireNonNull(dataHora, "Data/hora não pode ser nula");
        this.operacao = Objects.requireNonNull(operacao, "Operação não pode ser nula");
        this.detalhes = detalhes;
        this.usuario = usuario;
    }
    
    // Getters
    public String getId() { 
        return id; 
    }
    
    public LocalDateTime getDataHora() { 
        return dataHora; 
    }
    
    public String getOperacao() { 
        return operacao; 
    }
    
    public String getDetalhes() { 
        return detalhes; 
    }
    
    public String getUsuario() { 
        return usuario; 
    }
    
    @Override
    public String toString() {
        return String.format("Auditoria[id=%s, operacao=%s, dataHora=%s, usuario=%s]",
            id, operacao, dataHora, usuario);
    }
}
