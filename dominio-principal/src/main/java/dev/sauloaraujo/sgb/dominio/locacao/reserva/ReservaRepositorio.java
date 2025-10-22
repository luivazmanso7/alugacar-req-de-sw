package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import java.util.List;
import java.util.Optional;

public interface ReservaRepositorio {
	void salvar(Reserva reserva);

	Optional<Reserva> buscarPorCodigo(String codigo);

	List<Reserva> listar();
}
