package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;

public class RetiradaServico {
	private final ReservaRepositorio reservaRepositorio;
	private final VeiculoRepositorio veiculoRepositorio;
	private final LocacaoRepositorio locacaoRepositorio;

	public RetiradaServico(ReservaRepositorio reservaRepositorio, VeiculoRepositorio veiculoRepositorio,
			LocacaoRepositorio locacaoRepositorio) {
		this.reservaRepositorio = Objects.requireNonNull(reservaRepositorio, "Repositorio de reservas é obrigatório");
		this.veiculoRepositorio = Objects.requireNonNull(veiculoRepositorio, "Repositorio de veículos é obrigatório");
		this.locacaoRepositorio = Objects.requireNonNull(locacaoRepositorio, "Repositorio de locações é obrigatório");
	}

	public ContratoLocacao processar(ProcessarRetiradaCommand command) {
		Objects.requireNonNull(command, "O comando é obrigatório");

		var reserva = reservaRepositorio.buscarPorCodigo(command.getCodigoReserva())
				.orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));

		if (!reserva.getStatus().ativa()) {
			throw new IllegalStateException("A reserva já foi processada");
		}

		if (!command.isDocumentosValidos()) {
			throw new IllegalArgumentException("CNH vencida. Renovação necessária");
		}

		var veiculo = veiculoRepositorio.buscarPorPlaca(command.getPlacaVeiculo())
				.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

		if (veiculo.getStatus() == StatusVeiculo.VENDIDO) {
			throw new IllegalStateException("Veículo vendido não pode ser locado");
		}

		if (veiculo.getStatus() == StatusVeiculo.EM_MANUTENCAO) {
			throw new IllegalStateException("Veículo selecionado precisa passar por manutenção");
		}

		if (!veiculo.disponivel()) {
			throw new IllegalStateException("O veículo informado não está disponível");
		}

		if (!veiculo.getCategoria().equals(reserva.getCategoria())) {
			throw new IllegalStateException("Categoria do veículo não corresponde à reserva");
		}

		veiculo.locar();
		veiculoRepositorio.salvar(veiculo);

		var vistoria = new ChecklistVistoria(command.getQuilometragem(), command.getCombustivel(), false);
		var diasPrevistos = Math.toIntExact(reserva.diasReservados());
		var locacao = new Locacao(command.getCodigoLocacao(), reserva, veiculo, diasPrevistos, veiculo.getDiaria(),
				vistoria);
		locacaoRepositorio.salvar(locacao);

		reserva.concluir();
		reservaRepositorio.salvar(reserva);

		return new ContratoLocacao(locacao.getCodigo(), reserva.getCodigo(), veiculo.getPlaca(), locacao.getStatus());
	}
}
