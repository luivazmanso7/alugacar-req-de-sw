package dev.sauloaraujo.sgb.dominio.locacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CatalogoVeiculosServico;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.ClienteServico;
import dev.sauloaraujo.sgb.dominio.locacao.infra.InMemoryRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.manutencao.ManutencaoServico;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ChecklistVistoria;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.DevolucaoServico;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.RetiradaServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaCancelamentoServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaReplanejamentoServico;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaServico;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;

public abstract class AlugacarFuncionalidade {
	private static final InMemoryRepositorio REPOSITORIO = new InMemoryRepositorio();
	private static final ThreadLocal<RuntimeException> ULTIMO_ERRO = new ThreadLocal<>();

	protected final InMemoryRepositorio repositorio;
	protected final CatalogoVeiculosServico catalogoServico;
	protected final ClienteServico clienteServico;
	protected final ReservaServico reservaServico;
	protected final ReservaReplanejamentoServico reservaReplanejamentoServico;
	protected final ReservaCancelamentoServico reservaCancelamentoServico;
	protected final ManutencaoServico manutencaoServico;
	protected final RetiradaServico retiradaServico;
	protected final DevolucaoServico devolucaoServico;

	protected AlugacarFuncionalidade() {
		this.repositorio = REPOSITORIO;
		this.catalogoServico = new CatalogoVeiculosServico(repositorio, repositorio);
		this.clienteServico = new ClienteServico(repositorio);
		this.reservaServico = new ReservaServico(repositorio, repositorio, repositorio);
		this.reservaReplanejamentoServico = new ReservaReplanejamentoServico(repositorio, repositorio, repositorio);
		this.reservaCancelamentoServico = new ReservaCancelamentoServico(repositorio);
		this.manutencaoServico = new ManutencaoServico(repositorio);
		this.retiradaServico = new RetiradaServico(repositorio, repositorio, repositorio);
		this.devolucaoServico = new DevolucaoServico(repositorio, repositorio);
	}

	protected void limparContexto() {
		repositorio.limpar();
		ULTIMO_ERRO.remove();
	}

	protected void registrarCategoriaPadrao(CategoriaCodigo codigo, BigDecimal diaria, int quantidadeDisponivel) {
		var categoria = new Categoria(codigo, codigo.name(), "Categoria " + codigo.name(), diaria,
				List.of("Modelo " + codigo.name()), quantidadeDisponivel);
		catalogoServico.registrarCategoria(categoria);
	}

	protected void registrarVeiculosDisponiveis(CategoriaCodigo codigoCategoria, int quantidade) {
		var diaria = diariaDaCategoria(codigoCategoria);
		registrarCategoriaPadrao(codigoCategoria, diaria, quantidade);
		for (int indice = 0; indice < quantidade; indice++) {
			var placa = codigoCategoria.name() + "-" + (indice + 1);
			var veiculo = novoVeiculo(codigoCategoria, placa, diaria, "São Paulo", StatusVeiculo.DISPONIVEL);
			repositorio.salvar(veiculo);
		}
	}

	protected void registrarVeiculoIndividual(CategoriaCodigo codigoCategoria, String placa, StatusVeiculo status) {
		var diaria = diariaDaCategoria(codigoCategoria);
		var quantidadeAtual = repositorio.buscarPorCodigo(codigoCategoria)
				.map(Categoria::getQuantidadeDisponivel)
				.orElse(0);
		var novaQuantidade = Math.max(quantidadeAtual, 1);
		registrarCategoriaPadrao(codigoCategoria, diaria, novaQuantidade);
		var veiculo = novoVeiculo(codigoCategoria, placa, diaria, "São Paulo", status);
		repositorio.salvar(veiculo);
	}

	private Veiculo novoVeiculo(CategoriaCodigo categoriaCodigo, String placa, BigDecimal diaria, String cidade,
			StatusVeiculo status) {
		return new Veiculo(placa, "Modelo " + categoriaCodigo.name(), categoriaCodigo, cidade, diaria, status);
	}

	protected Reserva registrarReserva(String codigoReserva, CategoriaCodigo categoria, PeriodoLocacao periodo,
			StatusReserva status, Cliente cliente) {
		Objects.requireNonNull(periodo, "O período da reserva é obrigatório");
		var diaria = diariaDaCategoria(categoria);
		var reserva = new Reserva(codigoReserva, categoria, "São Paulo", periodo,
				diaria.multiply(BigDecimal.valueOf(periodo.dias())), status, cliente);
		repositorio.salvar(reserva);
		return reserva;
	}

	protected void registrarLocacao(String codigoLocacao, Reserva reserva, Veiculo veiculo, int odometro,
			int combustivelPercentual) {
		var vistoria = new ChecklistVistoria(odometro, Integer.toString(combustivelPercentual), false);
		var locacao = new Locacao(codigoLocacao, reserva, veiculo, Math.toIntExact(reserva.diasReservados()),
				veiculo.getDiaria(), vistoria);
		repositorio.salvar(locacao);
	}

	protected BigDecimal diariaDaCategoria(CategoriaCodigo codigo) {
		return catalogoServico.detalharCategoria(codigo).getDiaria();
	}

	protected void garantirCategoria(CategoriaCodigo codigo, BigDecimal diariaSugerida, int quantidadePadrao) {
		if (repositorio.buscarPorCodigo(codigo).isEmpty()) {
			var diaria = diariaSugerida != null ? diariaSugerida : BigDecimal.valueOf(100);
			registrarCategoriaPadrao(codigo, diaria, quantidadePadrao);
		}
	}

	protected Cliente clientePadraoDocumento(String documento) {
		var docLimpo = documento.replaceAll("\\D", "");
		return new Cliente("Cliente AlugaCar", docLimpo, "12345678901", docLimpo + "@alugacar.com", 
			"cliente_" + docLimpo, "senha123");
	}

	protected Cliente clientePadraoEmail(String email) {
		return new Cliente("Cliente AlugaCar", "12345678901", "12345678901", email,
			"cliente_teste", "senha123");
	}

	protected Cliente clientePersonalizado(String nome, String cpf, String cnh, String email) {
		return new Cliente(nome, cpf, cnh, email, 
			"cliente_" + cpf.replaceAll("\\D", ""), "senha123");
	}

	protected PeriodoLocacao periodo(String inicioIso, String fimIso) {
		return new PeriodoLocacao(LocalDateTime.parse(inicioIso), LocalDateTime.parse(fimIso));
	}

	protected void registrarErro(RuntimeException ex) {
		ULTIMO_ERRO.set(ex);
	}

	protected RuntimeException recuperarErro() {
		return ULTIMO_ERRO.get();
	}
}
