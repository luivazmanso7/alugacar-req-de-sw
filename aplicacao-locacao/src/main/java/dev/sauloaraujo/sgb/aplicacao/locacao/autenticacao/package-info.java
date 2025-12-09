/**
 * Módulo de autenticação de clientes.
 * 
 * <p>Responsável por:
 * <ul>
 *   <li>Autenticação de clientes (login/senha)</li>
 *   <li>Cadastro de novos clientes</li>
 *   <li>Validação de credenciais</li>
 *   <li>Verificação de status do cliente (ativo, bloqueado, inativo)</li>
 * </ul>
 * 
 * <p>Este módulo segue os princípios de Clean Architecture, delegando a lógica
 * de negócio para o domínio ({@link dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente})
 * e coordenando operações através do serviço de aplicação.
 * 
 * @see dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao.AutenticacaoServicoAplicacao
 * @see dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente
 * @see dev.sauloaraujo.sgb.dominio.locacao.cliente.Credenciais
 */
package dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao;
