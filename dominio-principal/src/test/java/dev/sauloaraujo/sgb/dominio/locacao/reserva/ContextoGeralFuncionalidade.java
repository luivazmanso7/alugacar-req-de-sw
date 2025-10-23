package dev.sauloaraujo.sgb.dominio.locacao.reserva;

import java.math.BigDecimal;

import dev.sauloaraujo.sgb.dominio.locacao.AlugacarFuncionalidade;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusReserva;
import dev.sauloaraujo.sgb.dominio.locacao.shared.StatusVeiculo;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;

public class ContextoGeralFuncionalidade extends AlugacarFuncionalidade {

	@Dado("que o catalogo de categorias esta limpo")
	public void que_o_catalogo_de_categorias_esta_limpo() {
		limparContexto();
	}

	@E("que nao existem reservas cadastradas")
	public void que_nao_existem_reservas_cadastradas() {
		// contexto limpo garante ausência de reservas
	}

	@E("que nao existem contratos de locacao ativos")
	public void que_nao_existem_contratos_de_locacao_ativos() {
		// contexto limpo garante ausência de locações
	}

	@Dado("a categoria {string} com diaria base {int}")
	public void a_categoria_com_diaria_base(String codigoCategoria, Integer diariaBase) {
		var categoria = CategoriaCodigo.fromTexto(codigoCategoria);
		registrarCategoriaPadrao(categoria, BigDecimal.valueOf(diariaBase), 10);
	}

	@E("existem {int} veiculos disponiveis da categoria {string}")
	public void existem_veiculos_disponiveis_da_categoria(Integer quantidade, String codigoCategoria) {
		var categoria = CategoriaCodigo.fromTexto(codigoCategoria);
		registrarVeiculosDisponiveis(categoria, quantidade);
	}

	@E("existem {int} reservas ativas da categoria {string} de {string} ate {string}")
	public void existem_reservas_ativas_da_categoria(Integer quantidade, String codigoCategoria, String inicio,
			String fim) {
		var categoria = CategoriaCodigo.fromTexto(codigoCategoria);
		garantirCategoria(categoria, null, 10);
		for (int indice = 0; indice < quantidade; indice++) {
			var codigo = "ATV-" + codigoCategoria + "-" + indice;
			var cliente = clientePadraoEmail("reserva-" + codigo + "@example.com");
			registrarReserva(codigo, categoria, periodo(inicio, fim), StatusReserva.ATIVA, cliente);
		}
	}

	@E("existe uma reserva confirmada {string} da categoria {string} de {string} ate {string} para o cliente {string}")
	public void existe_uma_reserva_confirmada(String codigoReserva, String codigoCategoria, String inicio, String fim,
			String documentoCliente) {
		var categoria = CategoriaCodigo.fromTexto(codigoCategoria);
		garantirCategoria(categoria, null, 5);
		var cliente = clientePadraoDocumento(documentoCliente);
		registrarReserva(codigoReserva, categoria, periodo(inicio, fim), StatusReserva.ATIVA, cliente);
	}

	@E("existe um veiculo elegivel para manutencao da categoria {string} com placa {string}")
	public void existe_um_veiculo_elegivel_para_manutencao(String codigoCategoria, String placa) {
		var categoria = CategoriaCodigo.fromTexto(codigoCategoria);
		garantirCategoria(categoria, null, 1);
		registrarVeiculoIndividual(categoria, placa, StatusVeiculo.DISPONIVEL);
	}

	@E("existe um veiculo disponivel da categoria {string} com placa {string}")
	public void existe_um_veiculo_disponivel_da_categoria(String codigoCategoria, String placa) {
		var categoria = CategoriaCodigo.fromTexto(codigoCategoria);
		garantirCategoria(categoria, null, 1);
		registrarVeiculoIndividual(categoria, placa, StatusVeiculo.DISPONIVEL);
	}

	@E("existe um veiculo com manutencao pendente da categoria {string} com placa {string}")
	public void existe_um_veiculo_com_manutencao_pendente_da_categoria(String codigoCategoria, String placa) {
		var categoria = CategoriaCodigo.fromTexto(codigoCategoria);
		garantirCategoria(categoria, null, 1);
		registrarVeiculoIndividual(categoria, placa, StatusVeiculo.EM_MANUTENCAO);
	}

	@E("existe um veiculo vendido da categoria {string} com placa {string}")
	public void existe_um_veiculo_vendido_da_categoria(String codigoCategoria, String placa) {
		var categoria = CategoriaCodigo.fromTexto(codigoCategoria);
		garantirCategoria(categoria, null, 1);
		registrarVeiculoIndividual(categoria, placa, StatusVeiculo.VENDIDO);
	}

	@E("a reserva {string} foi convertida em locacao com o veiculo {string} na data {string} com odometro {int} e combustivel {int}")
	public void a_reserva_foi_convertida_em_locacao(String codigoReserva, String placa, String dataRetirada,
			Integer odometro, Integer combustivel) {
		var reserva = repositorio.buscarPorCodigo(codigoReserva)
				.orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
		var veiculo = repositorio.buscarPorPlaca(placa)
				.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

		veiculo.locar();
		repositorio.salvar(veiculo);

		reserva.concluir();
		repositorio.salvar(reserva);

		registrarLocacao("LOCACAO-" + codigoReserva, reserva, veiculo, odometro, combustivel);
	}

	@E("existem {int} reservas ativas da categoria {string} de {string} ate {string} para a cidade {string}")
	public void existem_reservas_para_cidade(Integer quantidade, String codigoCategoria, String inicio, String fim,
			String cidade) {
		var categoria = CategoriaCodigo.fromTexto(codigoCategoria);
		garantirCategoria(categoria, null, 5);
		for (int indice = 0; indice < quantidade; indice++) {
			var codigo = "ATV-" + codigoCategoria + "-" + cidade + "-" + indice;
			var cliente = clientePadraoEmail("reserva-" + codigo + "@example.com");
			var reserva = new Reserva(codigo, categoria, cidade, periodo(inicio, fim),
					diariaDaCategoria(categoria).multiply(BigDecimal.valueOf(periodo(inicio, fim).dias())),
					StatusReserva.ATIVA, cliente);
			repositorio.salvar(reserva);
		}
	}

	@E("existem {int} veiculos disponiveis na categoria {string} na cidade {string}")
	public void existem_veiculos_disponiveis_na_categoria_na_cidade(Integer quantidade, String codigoCategoria,
			String cidade) {
		var categoria = CategoriaCodigo.fromTexto(codigoCategoria);
		garantirCategoria(categoria, null, quantidade);
		var diaria = diariaDaCategoria(categoria);
		for (int indice = 0; indice < quantidade; indice++) {
			var placa = codigoCategoria + "-" + cidade.substring(0, Math.min(3, cidade.length())).toUpperCase()
					+ (indice + 1);
			var veiculo = new Veiculo(placa, "Modelo " + categoria.name(), categoria, cidade, diaria,
					StatusVeiculo.DISPONIVEL);
			repositorio.salvar(veiculo);
		}
	}
}
