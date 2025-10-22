package dev.sauloaraujo.sgb.dominio.locacao;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CatalogoVeiculosServico;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.ClienteServico;
import dev.sauloaraujo.sgb.dominio.locacao.infra.InMemoryRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.manutencao.ManutencaoServico;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.DevolucaoServico;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.RetiradaServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaCancelamentoServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaReplanejamentoServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaServico;

public class AlugacarFuncionalidade {
	private static final InMemoryRepositorio REPOSITORIO = new InMemoryRepositorio();

	protected final InMemoryRepositorio repositorio;
	protected final CatalogoVeiculosServico catalogoServico;
	protected final ClienteServico clienteServico;
	protected final ReservaServico reservaServico;
	protected final ReservaReplanejamentoServico reservaReplanejamentoServico;
	protected final ReservaCancelamentoServico reservaCancelamentoServico;
	protected final ManutencaoServico manutencaoServico;
	protected final RetiradaServico retiradaServico;
	protected final DevolucaoServico devolucaoServico;

	public AlugacarFuncionalidade() {
		repositorio = REPOSITORIO;
		catalogoServico = new CatalogoVeiculosServico(repositorio, repositorio);
		clienteServico = new ClienteServico(repositorio);
		reservaServico = new ReservaServico(repositorio, repositorio, repositorio);
		reservaReplanejamentoServico = new ReservaReplanejamentoServico(repositorio, repositorio, repositorio);
		reservaCancelamentoServico = new ReservaCancelamentoServico(repositorio);
		manutencaoServico = new ManutencaoServico(repositorio);
		retiradaServico = new RetiradaServico(repositorio, repositorio, repositorio);
		devolucaoServico = new DevolucaoServico(repositorio, repositorio);
	}

	protected void limparContexto() {
		repositorio.limpar();
	}
}
