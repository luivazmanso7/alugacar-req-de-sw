package dev.sauloaraujo.sgb.aplicacao.locacao.auditoria;

import dev.sauloaraujo.sgb.dominio.locacao.auditoria.Auditoria;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface de repositório para auditoria na camada de aplicação.
 * 
 * <p>
 * Define operações de persistência e consulta de registros de auditoria,
 * seguindo Clean Architecture e DDD.
 * </p>
 * 
 * @since 2.0
 * @version 2.0
 */
public interface AuditoriaRepositorioAplicacao {
    
    /**
     * Salva um registro de auditoria.
     * 
     * @param auditoria entidade de domínio a ser persistida
     * @return a auditoria salva
     * @throws IllegalArgumentException se auditoria for nula
     */
    Auditoria salvar(Auditoria auditoria);
    
    /**
     * Busca todas as auditorias de uma operação específica.
     * 
     * @param operacao tipo da operação
     * @return lista de auditorias (vazia se não houver registros)
     */
    List<Auditoria> buscarPorOperacao(String operacao);
    
    /**
     * Busca auditorias em um período de tempo.
     * 
     * @param inicio data/hora inicial (inclusivo)
     * @param fim data/hora final (inclusivo)
     * @return lista de auditorias no período
     */
    List<Auditoria> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
    
    /**
     * Busca todas as auditorias.
     * 
     * @return lista de todas as auditorias
     */
    List<Auditoria> buscarTodas();
}
