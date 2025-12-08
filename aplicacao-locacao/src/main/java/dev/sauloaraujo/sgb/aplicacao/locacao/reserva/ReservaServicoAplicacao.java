package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de Aplicação para Reserva.
 * Coordena operações relacionadas a reservas na camada de aplicação.
 */
public class ReservaServicoAplicacao {
	private final ReservaRepositorioAplicacao repositorio;

	public ReservaServicoAplicacao(ReservaRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public List<ReservaResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
	
	public Optional<ReservaResumo> buscarPorCodigo(String codigo) {
		notNull(codigo, "O código não pode ser nulo");
		return repositorio.buscarPorCodigo(codigo);
	}
	
	public List<ReservaResumo> listarPorCliente(String cpfOuCnpj) {
		notNull(cpfOuCnpj, "O CPF/CNPJ não pode ser nulo");
		return repositorio.listarPorCliente(cpfOuCnpj);
	}
	
	public List<ReservaResumo> listarAtivas() {
		return repositorio.listarAtivas();
	}
}
