package dev.sauloaraujo.sgb.dominio.locacao.cliente;

import java.util.Objects;
import java.util.regex.Pattern;

import dev.sauloaraujo.sgb.dominio.locacao.shared.Credenciais;

public class Cliente {
	private static final Pattern CPF_FORMATO = Pattern.compile("\\d{11}");
	private static final Pattern CNH_FORMATO = Pattern.compile("\\d{11}");

	private final String nome;
	private final String cpfOuCnpj;
	private final String cnh;
	private final String email;
	private final Credenciais credenciais;
	private StatusCliente status;

	/**
	 * Construtor para novo cliente (com criação de credenciais).
	 */
	public Cliente(String nome, String cpfOuCnpj, String cnh, String email, 
	               String login, String senha) {
		this.nome = validarNome(nome);
		this.cpfOuCnpj = validarDocumento(cpfOuCnpj);
		this.cnh = validarCnh(cnh);
		this.email = Objects.requireNonNull(email, "O e-mail do cliente é obrigatório");
		this.credenciais = Credenciais.criar(login, senha);
		this.status = StatusCliente.ATIVO;
	}
	
	/**
	 * Construtor de reconstrução (usado por repositórios).
	 */
	public Cliente(String nome, String cpfOuCnpj, String cnh, String email, 
	               Credenciais credenciais, StatusCliente status) {
		this.nome = validarNome(nome);
		this.cpfOuCnpj = validarDocumento(cpfOuCnpj);
		this.cnh = validarCnh(cnh);
		this.email = Objects.requireNonNull(email, "O e-mail do cliente é obrigatório");
		this.credenciais = Objects.requireNonNull(credenciais, "Credenciais são obrigatórias");
		this.status = Objects.requireNonNull(status, "Status é obrigatório");
	}

	public String getNome() {
		return nome;
	}

	public String getCpfOuCnpj() {
		return cpfOuCnpj;
	}

	public String getCnh() {
		return cnh;
	}

	public String getEmail() {
		return email;
	}
	
	public Credenciais getCredenciais() {
		return credenciais;
	}
	
	public StatusCliente getStatus() {
		return status;
	}
	
	/**
	 * Autentica o cliente verificando suas credenciais.
	 * 
	 * @param login Login fornecido
	 * @param senha Senha em texto plano
	 * @return true se autenticado com sucesso
	 * @throws IllegalStateException se o cliente está bloqueado ou inativo
	 */
	public boolean autenticar(String login, String senha) {
		if (status == StatusCliente.BLOQUEADO) {
			throw new IllegalStateException("Cliente bloqueado. Entre em contato com o suporte.");
		}
		
		if (status == StatusCliente.INATIVO) {
			throw new IllegalStateException("Cliente inativo. Entre em contato com o suporte.");
		}
		
		return credenciais.getLogin().equals(login) && 
		       credenciais.verificarSenha(senha);
	}
	
	/**
	 * Bloqueia o cliente (por inadimplência, multas, etc).
	 */
	public void bloquear() {
		if (status == StatusCliente.INATIVO) {
			throw new IllegalStateException("Não é possível bloquear um cliente inativo");
		}
		this.status = StatusCliente.BLOQUEADO;
	}
	
	/**
	 * Desbloqueia o cliente.
	 */
	public void desbloquear() {
		if (status != StatusCliente.BLOQUEADO) {
			throw new IllegalStateException("Cliente não está bloqueado");
		}
		this.status = StatusCliente.ATIVO;
	}
	
	/**
	 * Inativa o cliente (remoção lógica).
	 */
	public void inativar() {
		this.status = StatusCliente.INATIVO;
	}

	private String validarNome(String nome) {
		var valor = Objects.requireNonNull(nome, "O nome do cliente é obrigatório").trim();
		if (valor.isEmpty()) {
			throw new IllegalArgumentException("O nome do cliente é obrigatório");
		}
		return valor;
	}

	private String validarDocumento(String documento) {
		var valor = Objects.requireNonNull(documento, "O documento do cliente é obrigatório").replaceAll("\\D", "");
		if (!CPF_FORMATO.matcher(valor).matches()) {
			throw new IllegalArgumentException("CPF/CNPJ inválido. Utilize somente números");
		}
		return valor;
	}

	private String validarCnh(String cnh) {
		var valor = Objects.requireNonNull(cnh, "A CNH é obrigatória").replaceAll("\\D", "");
		if (!CNH_FORMATO.matcher(valor).matches()) {
			throw new IllegalArgumentException("CNH inválida. Utilize somente números");
		}
		return valor;
	}
}
