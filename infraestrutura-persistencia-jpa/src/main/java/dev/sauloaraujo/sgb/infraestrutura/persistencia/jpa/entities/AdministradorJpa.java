package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.dominio.locacao.admin.Administrador;
import dev.sauloaraujo.sgb.dominio.locacao.admin.AdministradorRepositorio;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.JpaMapeador;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository.AdministradorJpaRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ADMINISTRADOR")
public class AdministradorJpa {

	@Id
	@Column(name = "id", nullable = false, length = 50)
	private String id;

	@Column(name = "nome", nullable = false, length = 200)
	private String nome;

	@Column(name = "email", nullable = false, length = 150)
	private String email;
	
	@Column(name = "login", nullable = false, length = 30, unique = true)
	private String login;
	
	@Column(name = "senha_hash", nullable = false, length = 100)
	private String senhaHash;
	
	@Column(name = "status", nullable = false, length = 20)
	private String status;

	public AdministradorJpa() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getSenhaHash() {
		return senhaHash;
	}
	
	public void setSenhaHash(String senhaHash) {
		this.senhaHash = senhaHash;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}

@Repository("administradorRepositorioReal")
class AdministradorRepositorioImpl implements AdministradorRepositorio {

	@Autowired
	AdministradorJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Administrador administrador) {
		var administradorJpa = mapeador.map(administrador, AdministradorJpa.class);
		repositorio.save(administradorJpa);
	}

	@Override
	public Optional<Administrador> buscarPorId(String id) {
		return repositorio.findById(id)
			.map(jpa -> mapeador.map(jpa, Administrador.class));
	}

	@Override
	public Optional<Administrador> buscarPorLogin(String login) {
		return repositorio.findByLogin(login)
			.map(jpa -> mapeador.map(jpa, Administrador.class));
	}
}

