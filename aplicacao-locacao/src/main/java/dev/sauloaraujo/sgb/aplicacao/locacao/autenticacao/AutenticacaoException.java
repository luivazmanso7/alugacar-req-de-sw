package dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao;

/**
 * Exceção lançada quando há falha na autenticação.
 */
public class AutenticacaoException extends RuntimeException {
	
	public AutenticacaoException(String mensagem) {
		super(mensagem);
	}
	
	public AutenticacaoException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
