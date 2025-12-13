package dev.sauloaraujo.sgb.aplicacao.locacao.reserva;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ChecklistVistoria;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.RetiradaInfo;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusLocacao;

@Service
public class ConfirmarRetiradaService {
	
	private final ReservaRepositorio reservaRepositorio;
	private final VeiculoRepositorio veiculoRepositorio;
	private final LocacaoRepositorio locacaoRepositorio;
	
	public ConfirmarRetiradaService(
			ReservaRepositorio reservaRepositorio,
			VeiculoRepositorio veiculoRepositorio,
			LocacaoRepositorio locacaoRepositorio) {
		this.reservaRepositorio = notNull(reservaRepositorio, "Repositório de reserva é obrigatório");
		this.veiculoRepositorio = notNull(veiculoRepositorio, "Repositório de veículo é obrigatório");
		this.locacaoRepositorio = notNull(locacaoRepositorio, "Repositório de locação é obrigatório");
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
		
		String codigoLocacao = "LOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		int diasPrevistos = (int) reserva.diasReservados();
		ChecklistVistoria vistoriaRetirada = new ChecklistVistoria(
			(int) comando.quilometragemSaida(),
			comando.nivelTanqueSaida(),
			false
		);
		
		Locacao locacao = new Locacao(
			codigoLocacao,
			reserva,
			veiculo,
			diasPrevistos,
			veiculo.getDiaria(),
			vistoriaRetirada,
			new dev.sauloaraujo.sgb.dominio.locacao.operacao.MultaPadraoStrategy(),
			StatusLocacao.EM_ANDAMENTO
		);
		
		reservaRepositorio.salvar(reserva);
		veiculoRepositorio.salvar(veiculo);
		locacaoRepositorio.salvar(locacao);
	}
}

