package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import static org.apache.commons.lang3.Validate.notNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.RetiradaInfo;

@Service
public class ConfirmarRetiradaService {
	
	private final ReservaRepositorio reservaRepositorio;
	private final VeiculoRepositorio veiculoRepositorio;
	
	public ConfirmarRetiradaService(
			ReservaRepositorio reservaRepositorio,
			VeiculoRepositorio veiculoRepositorio) {
		this.reservaRepositorio = notNull(reservaRepositorio, "Repositório de reserva é obrigatório");
		this.veiculoRepositorio = notNull(veiculoRepositorio, "Repositório de veículo é obrigatório");
	}
	
	@Transactional
	public void confirmarRetirada(ConfirmarRetiradaCmd comando) {
		Reserva reserva = reservaRepositorio.buscarPorCodigo(comando.codigoReserva())
			.orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada: " + comando.codigoReserva()));
		
		Veiculo veiculo = veiculoRepositorio.buscarPorPlaca(comando.placaVeiculo())
			.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + comando.placaVeiculo()));
		
		RetiradaInfo retiradaInfo = new RetiradaInfo(
			comando.placaVeiculo(),
			comando.cnhCondutor(),
			comando.dataHoraRetirada(),
			comando.quilometragemSaida(),
			comando.nivelTanqueSaida(),
			comando.observacoes()
		);
		
		reserva.confirmarRetirada(veiculo, retiradaInfo);
		veiculo.marcarComoAlugado();
		
		reservaRepositorio.salvar(reserva);
		veiculoRepositorio.salvar(veiculo);
	}
}

