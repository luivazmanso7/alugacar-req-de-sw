package dev.sauloaraujo.sgb.aplicacao.locacao.cliente;

import java.util.List;
import java.util.Optional;

/**
 * Interface do Repositório de Aplicação para Cliente.
 * Define os métodos de consulta necessários para a camada de aplicação.
 */
public interface ClienteRepositorioAplicacao {
	List<ClienteResumo> pesquisarResumos();
	
	Optional<ClienteResumo> buscarPorCpfOuCnpj(String cpfOuCnpj);
}
