package dev.sauloaraujo.sgb.aplicacao.locacao.operacao;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de Aplicação para Locação.
 * Coordena operações relacionadas a locações na camada de aplicação.
 */
public class LocacaoServicoAplicacao {
	private final LocacaoRepositorioAplicacao repositorio;

	public LocacaoServicoAplicacao(LocacaoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<LocacaoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
	
	public Optional<LocacaoResumo> buscarPorCodigo(String codigo) {
		notNull(codigo, "O código não pode ser nulo");
		return repositorio.buscarPorCodigo(codigo);
	}
	
	public List<LocacaoResumo> listarAtivas() {
		return repositorio.listarAtivas();
	}
	
	public List<LocacaoResumo> listarPorCliente(String cpfOuCnpj) {
		notNull(cpfOuCnpj, "O CPF/CNPJ não pode ser nulo");
		return repositorio.listarPorCliente(cpfOuCnpj);
	}
}
