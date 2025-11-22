package dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities.ClienteJpa;
import dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.repositories.ClienteJpaRepository;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.ClienteRepositorio;

/**
 * Implementação JPA do repositório de Clientes.
 */
@Repository
public class ClienteRepositorioImpl implements ClienteRepositorio {

	@Autowired
	private ClienteJpaRepository jpaRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public void salvar(Cliente cliente) {
		ClienteJpa clienteJpa = modelMapper.map(cliente, ClienteJpa.class);
		jpaRepository.save(clienteJpa);
	}

	@Override
	public Optional<Cliente> buscarPorDocumento(String cpfOuCnpj) {
		return jpaRepository.findById(cpfOuCnpj)
				.map(jpa -> modelMapper.map(jpa, Cliente.class));
	}

	@Override
	public List<Cliente> listarClientes() {
		return jpaRepository.findAll().stream()
				.map(jpa -> modelMapper.map(jpa, Cliente.class))
				.collect(Collectors.toList());
	}
}
