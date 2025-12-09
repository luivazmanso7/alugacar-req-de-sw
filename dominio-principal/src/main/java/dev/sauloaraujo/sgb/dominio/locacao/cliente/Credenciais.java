package dev.sauloaraujo.sgb.dominio.locacao.cliente;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representando as credenciais de autenticação de um cliente.
 * Imutável e com validação de senha segura.
 */
public final class Credenciais {
	
	private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{4,30}$");
	private static final int SENHA_MIN_LENGTH = 6;
	
	private final String login;
	private final String senhaCriptografada;
	
	/**
	 * Construtor para criação de novas credenciais (senha em texto plano).
	 * 
	 * @param login Login do usuário (4-30 caracteres alfanuméricos, . _ -)
	 * @param senhaTextoPlano Senha em texto plano (mínimo 6 caracteres)
	 * @return Credenciais com senha criptografada
	 */
	public static Credenciais criar(String login, String senhaTextoPlano) {
		var loginValidado = validarLogin(login);
		var senhaCriptografada = criptografarSenha(validarSenha(senhaTextoPlano));
		return new Credenciais(loginValidado, senhaCriptografada);
	}
	
	/**
	 * Construtor de reconstrução (usado por repositórios).
	 * A senha já deve estar criptografada.
	 * 
	 * @param login Login do usuário
	 * @param senhaCriptografada Senha já criptografada
	 */
	public Credenciais(String login, String senhaCriptografada) {
		this.login = Objects.requireNonNull(login, "Login não pode ser nulo");
		this.senhaCriptografada = Objects.requireNonNull(senhaCriptografada, "Senha não pode ser nula");
	}
	
	/**
	 * Verifica se a senha fornecida corresponde à senha armazenada.
	 * 
	 * @param senhaTextoPlano Senha em texto plano para verificar
	 * @return true se a senha é válida, false caso contrário
	 */
	public boolean verificarSenha(String senhaTextoPlano) {
		if (senhaTextoPlano == null || senhaTextoPlano.isEmpty()) {
			return false;
		}
		var senhaTentativa = criptografarSenha(senhaTextoPlano);
		return this.senhaCriptografada.equals(senhaTentativa);
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getSenhaCriptografada() {
		return senhaCriptografada;
	}
	
	private static String validarLogin(String login) {
		var valor = Objects.requireNonNull(login, "Login é obrigatório").trim();
		
		if (valor.isEmpty()) {
			throw new IllegalArgumentException("Login não pode ser vazio");
		}
		
		if (!LOGIN_PATTERN.matcher(valor).matches()) {
			throw new IllegalArgumentException(
				"Login inválido. Use 4-30 caracteres alfanuméricos, pontos, hífens ou underscores"
			);
		}
		
		return valor;
	}
	
	private static String validarSenha(String senha) {
		var valor = Objects.requireNonNull(senha, "Senha é obrigatória");
		
		if (valor.length() < SENHA_MIN_LENGTH) {
			throw new IllegalArgumentException(
				"Senha deve ter no mínimo " + SENHA_MIN_LENGTH + " caracteres"
			);
		}
		
		return valor;
	}
	
	/**
	 * Criptografia simplificada para demonstração.
	 * ATENÇÃO: Em produção, use BCrypt, Argon2 ou PBKDF2.
	 */
	private static String criptografarSenha(String senha) {
		// Simulação de hash (em produção, usar BCrypt)
		return "HASH_" + senha.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Credenciais that = (Credenciais) o;
		return Objects.equals(login, that.login) && 
		       Objects.equals(senhaCriptografada, that.senhaCriptografada);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(login, senhaCriptografada);
	}
	
	@Override
	public String toString() {
		return "Credenciais{login='" + login + "', senha=***}";
	}
}
