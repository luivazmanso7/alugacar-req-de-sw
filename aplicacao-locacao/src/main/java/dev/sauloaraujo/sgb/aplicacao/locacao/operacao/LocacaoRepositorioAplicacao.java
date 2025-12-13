package dev.sauloaraujo.sgb.aplicacao.locacao.operacao;

import java.util.List;
import java.util.Optional;

/**
 * Interface do Repositório de Aplicação para Locação.
 * Define os métodos de consulta necessários para a camada de aplicação.
 */
public interface LocacaoRepositorioAplicacao {
	List<LocacaoResumo> pesquisarResumos();
	
	Optional<LocacaoResumo> buscarPorCodigo(String codigo);
	
	List<LocacaoResumo> listarAtivas();
	
	List<LocacaoResumo> listarEmAndamento();
	
	List<LocacaoResumo> listarPorCliente(String cpfOuCnpj);
}
