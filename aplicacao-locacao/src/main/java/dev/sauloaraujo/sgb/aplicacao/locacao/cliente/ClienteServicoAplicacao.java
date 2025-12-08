package dev.sauloaraujo.sgb.aplicacao.locacao.cliente;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de Aplicação para Cliente.
 * Coordena operações relacionadas a clientes na camada de aplicação.
 */
public class ClienteServicoAplicacao {
	private final ClienteRepositorioAplicacao repositorio;

	public ClienteServicoAplicacao(ClienteRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<ClienteResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
	
	public Optional<ClienteResumo> buscarPorCpfOuCnpj(String cpfOuCnpj) {
		notNull(cpfOuCnpj, "O CPF/CNPJ não pode ser nulo");
		return repositorio.buscarPorCpfOuCnpj(cpfOuCnpj);
	}
}
