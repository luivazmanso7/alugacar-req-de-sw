/**
 * Pacote que contém eventos de domínio relacionados à locação de veículos.
 * 
 * <p>
 * Eventos de domínio representam fatos importantes que ocorreram no sistema
 * e são utilizados para implementar o padrão Observer de forma desacoplada.
 * </p>
 * 
 * <h2>Eventos disponíveis:</h2>
 * <ul>
 *   <li>{@link dev.sauloaraujo.sgb.dominio.locacao.evento.LocacaoRealizadaEvent} - Locação criada com sucesso</li>
 * </ul>
 * 
 * <h2>Características dos eventos:</h2>
 * <ul>
 *   <li>Imutáveis (implementados como Java records)</li>
 *   <li>Validação de dados no construtor</li>
 *   <li>Java puro (sem dependências de frameworks)</li>
 *   <li>Thread-safe por padrão</li>
 * </ul>
 * 
 * @since 2.0
 * @version 2.0
 */
package dev.sauloaraujo.sgb.dominio.locacao.evento;
