package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa;

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
	String cpfOuCnpj;

	@Column(name = "nome", nullable = false, length = 200)
	String nome;

	@Column(name = "cnh", nullable = false, length = 11)
	String cnh;

	@Column(name = "email", nullable = false, length = 150)
	String email;
}

/**
 * Repositório Spring Data JPA para Cliente.
 */
interface ClienteJpaRepository extends JpaRepository<ClienteJpa, String> {
}

/**
 * Implementação do repositório do domínio para Cliente.
 * Faz a ponte entre o domínio e a infraestrutura JPA.
 */
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
		var clientes = repositorio.findAll();
		return mapeador.mapList(clientes, Cliente.class);
	}
}
