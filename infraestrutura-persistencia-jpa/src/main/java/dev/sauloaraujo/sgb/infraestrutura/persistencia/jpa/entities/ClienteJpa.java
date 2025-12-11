package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.ClienteRepositorio;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.JpaMapeador;
import dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository.ClienteJpaRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA para persistência de Clientes.
 */
@Entity
@Table(name = "CLIENTE")
public class ClienteJpa {

	@Id
	@Column(name = "cpf_cnpj", nullable = false, length = 14)
	private String cpfOuCnpj;

	@Column(name = "nome", nullable = false, length = 200)
	private String nome;

	@Column(name = "cnh", nullable = false, length = 11)
	private String cnh;

	@Column(name = "email", nullable = false, length = 150)
	private String email;
	
	@Column(name = "login", nullable = false, length = 30, unique = true)
	private String login;
	
	@Column(name = "senha_hash", nullable = false, length = 100)
	private String senhaHash;
	
	@Column(name = "status", nullable = false, length = 20)
	private String status;

	public ClienteJpa() {
	}

	public String getCpfOuCnpj() {
		return cpfOuCnpj;
	}

	public void setCpfOuCnpj(String cpfOuCnpj) {
		this.cpfOuCnpj = cpfOuCnpj;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCnh() {
		return cnh;
	}

	public void setCnh(String cnh) {
		this.cnh = cnh;
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

@Repository
class ClienteRepositorioImpl implements ClienteRepositorio {

	@Autowired
	ClienteJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Cliente cliente) {
		var clienteJpa = mapeador.map(cliente, ClienteJpa.class);
		repositorio.save(clienteJpa);
	}

	@Override
	public Optional<Cliente> buscarPorDocumento(String cpfOuCnpj) {
		return repositorio.findById(cpfOuCnpj)
				.map(jpa -> mapeador.map(jpa, Cliente.class));
	}
	
	@Override
	public Optional<Cliente> buscarPorLogin(String login) {
		return repositorio.findByLogin(login)
				.map(jpa -> mapeador.map(jpa, Cliente.class));
	}

	@Override
	public List<Cliente> listarClientes() {
		var clientesJpa = repositorio.findAll();
		return mapeador.mapList(clientesJpa, Cliente.class);
	}
}
