package dev.sauloaraujo.sgb.dominio.locacao.cliente;

import java.util.Objects;

public class ClienteServico {
	private final ClienteRepositorio clienteRepositorio;

	public ClienteServico(ClienteRepositorio clienteRepositorio) {
		this.clienteRepositorio = Objects.requireNonNull(clienteRepositorio,
				"O repositório de clientes é obrigatório");
	}

	public Cliente registrar(Cliente cliente) {
		var novoCliente = Objects.requireNonNull(cliente, "O cliente não pode ser nulo");
		clienteRepositorio.salvar(novoCliente);
		return novoCliente;
	}

	public Cliente obterPorDocumento(String documento) {
		var doc = Objects.requireNonNull(documento, "O documento é obrigatório").replaceAll("\\D", "");
		return clienteRepositorio.buscarPorDocumento(doc)
				.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado para documento " + doc));
	}
}
