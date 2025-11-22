package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities;

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

	protected ClienteJpa() {
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
}
