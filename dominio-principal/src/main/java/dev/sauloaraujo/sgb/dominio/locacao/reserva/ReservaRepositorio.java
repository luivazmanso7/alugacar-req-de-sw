package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import java.util.List;
import java.util.Optional;

public interface ReservaRepositorio {
	void salvar(Reserva reserva);

	Optional<Reserva> buscarPorCodigo(String codigo);

	List<Reserva> listar();
	
	/**
	 * Lista todas as reservas de um cliente específico.
	 * 
	 * @param cpfOuCnpj CPF ou CNPJ do cliente
	 * @return lista de reservas do cliente
	 */
	List<Reserva> listarPorCliente(String cpfOuCnpj);

	/**
	 * Lista todas as reservas de um veículo específico.
	 * 
	 * @param placaVeiculo placa do veículo
	 * @return lista de reservas do veículo
	 */
	List<Reserva> listarPorVeiculo(String placaVeiculo);
}
