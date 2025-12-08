package dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.ClienteRepositorio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA para persistência de Clientes.
 */
@Entity
@Table(name = "CLIENTE")
class ClienteJpa {

	@Id
	@Column(name = "cpf_cnpj", nullable = false, length = 14)
	private String cpfOuCnpj;

	@Column(name = "nome", nullable = false, length = 200)
	private String nome;

	@Column(name = "cnh", nullable = false, length = 11)
	private String cnh;

	@Column(name = "email", nullable = false, length = 150)
	private String email;

	ClienteJpa() {
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

interface ClienteJpaRepository extends JpaRepository<ClienteJpa, String> {
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
	public List<Cliente> listarClientes() {
		var clientesJpa = repositorio.findAll();
		return mapeador.mapList(clientesJpa, Cliente.class);
	}
}
