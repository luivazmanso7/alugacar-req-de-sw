package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import java.util.List;
import java.util.Optional;

/**
 * Interface do Repositório de Aplicação para Reserva.
 * Define os métodos de consulta necessários para a camada de aplicação.
 */
public interface ReservaRepositorioAplicacao {
	List<ReservaResumo> pesquisarResumos();
	
	Optional<ReservaResumo> buscarPorCodigo(String codigo);
	
	List<ReservaResumo> listarPorCliente(String cpfOuCnpj);
	
	List<ReservaResumo> listarAtivas();
}
