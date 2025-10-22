package dev.sauloaraujo.sgb.dominio.locacao.cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositorio {
	void salvar(Cliente cliente);

	Optional<Cliente> buscarPorDocumento(String cpfOuCnpj);

	List<Cliente> listarClientes();
}
