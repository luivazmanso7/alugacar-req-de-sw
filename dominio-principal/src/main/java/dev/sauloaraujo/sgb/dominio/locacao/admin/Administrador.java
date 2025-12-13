package dev.sauloaraujo.sgb.dominio.locacao.admin;

import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.shared.Credenciais;

public class Administrador {
	
	private final String id;
	private final String nome;
	private final String email;
	private final Credenciais credenciais;
	private StatusAdministrador status;
	
	public Administrador(String id, String nome, String email, String login, String senha) {
		this.id = validarId(id);
		this.nome = validarNome(nome);
		this.email = Objects.requireNonNull(email, "O e-mail do administrador é obrigatório");
		this.credenciais = Credenciais.criar(login, senha);
		this.status = StatusAdministrador.ATIVO;
	}
	
	public Administrador(String id, String nome, String email, Credenciais credenciais, StatusAdministrador status) {
		this.id = validarId(id);
		this.nome = validarNome(nome);
		this.email = Objects.requireNonNull(email, "O e-mail do administrador é obrigatório");
		this.credenciais = Objects.requireNonNull(credenciais, "Credenciais são obrigatórias");
		this.status = Objects.requireNonNull(status, "Status é obrigatório");
	}
	
	public String getId() {
		return id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getEmail() {
		return email;
	}
	
	public Credenciais getCredenciais() {
		return credenciais;
	}
	
	public StatusAdministrador getStatus() {
		return status;
	}
	
	public boolean autenticar(String login, String senha) {
		if (status == StatusAdministrador.INATIVO) {
			throw new IllegalStateException("Administrador inativo. Entre em contato com o suporte.");
		}
		
		return credenciais.getLogin().equals(login) && 
		       credenciais.verificarSenha(senha);
	}
	
	public void inativar() {
		this.status = StatusAdministrador.INATIVO;
	}
	
	public void ativar() {
		this.status = StatusAdministrador.ATIVO;
	}
	
	private String validarId(String id) {
		var valor = Objects.requireNonNull(id, "O ID do administrador é obrigatório").trim();
		if (valor.isEmpty()) {
			throw new IllegalArgumentException("O ID do administrador não pode estar vazio");
		}
		return valor;
	}
	
	private String validarNome(String nome) {
		var valor = Objects.requireNonNull(nome, "O nome do administrador é obrigatório").trim();
		if (valor.isEmpty()) {
			throw new IllegalArgumentException("O nome do administrador não pode estar vazio");
		}
		return valor;
	}
}

