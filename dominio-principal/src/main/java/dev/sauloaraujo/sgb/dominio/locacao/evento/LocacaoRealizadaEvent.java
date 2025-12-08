package dev.sauloaraujo.sgb.dominio.locacao.evento;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Evento de domínio que representa uma locação realizada.
 * 
 * <p>
 * Este evento é publicado quando uma locação é criada com sucesso.
 * Implementado como record imutável (Java 14+) para garantir
 * consistência e thread-safety.
 * </p>
 * 
 * <p><strong>Uso no padrão Observer:</strong></p>
 * <ul>
 *   <li>Publicado por: {@code LocacaoServicoAplicacao}</li>
 *   <li>Observado por: {@code NotificacaoListener}, {@code AuditoriaLocacaoListener}</li>
 * </ul>
 * 
 * @param codigoLocacao código único da locação realizada
 * @param emailCliente email do cliente que realizou a locação
 * @param dataLocacao data e hora em que a locação foi realizada
 * 
 * @since 2.0
 * @version 2.0
 */
public record LocacaoRealizadaEvent(
    String codigoLocacao,
    String emailCliente,
    LocalDateTime dataLocacao
) {
    /**
     * Construtor compacto com validações.
     * 
     * @throws NullPointerException se qualquer parâmetro for nulo
     * @throws IllegalArgumentException se codigoLocacao ou emailCliente forem vazios
     */
    public LocacaoRealizadaEvent {
        notBlank(codigoLocacao, "Código da locação não pode ser vazio");
        notBlank(emailCliente, "Email do cliente não pode ser vazio");
        Objects.requireNonNull(dataLocacao, "Data da locação não pode ser nula");
    }
    
    /**
     * Valida que uma string não é nula nem vazia.
     * 
     * @param value valor a ser validado
     * @param message mensagem de erro
     * @throws IllegalArgumentException se o valor for nulo ou vazio
     */
    private static void notBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
