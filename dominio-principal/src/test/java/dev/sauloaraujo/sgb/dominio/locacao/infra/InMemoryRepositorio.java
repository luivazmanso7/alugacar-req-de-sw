package dev.sauloaraujo.sgb.dominio.locacao.infra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Categoria;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.Veiculo;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.VeiculoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.ClienteRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Locacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.LocacaoRepositorio;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.Reserva;
import dev.sauloaraujo.sgb.dominio.locacao.reserva.ReservaRepositorio;

public class InMemoryRepositorio implements CategoriaRepositorio, VeiculoRepositorio, ReservaRepositorio,
		LocacaoRepositorio, ClienteRepositorio {
	private final Map<CategoriaCodigo, Categoria> categorias = new HashMap<>();
	private final Map<String, Veiculo> veiculos = new HashMap<>();
	private final Map<String, Reserva> reservas = new HashMap<>();
	private final Map<String, Locacao> locacoes = new HashMap<>();
	private final Map<String, Cliente> clientes = new HashMap<>();

	@Override
	public void salvar(Categoria categoria) {
		categorias.put(categoria.getCodigo(), categoria);
	}

	@Override
	public Optional<Categoria> buscarPorCodigo(CategoriaCodigo codigo) {
		return Optional.ofNullable(categorias.get(codigo));
	}

	@Override
	public List<Categoria> listarTodas() {
		return new ArrayList<>(categorias.values());
	}

	@Override
	public void salvar(Veiculo veiculo) {
		veiculos.put(veiculo.getPlaca(), veiculo);
	}

	@Override
	public Optional<Veiculo> buscarPorPlaca(String placa) {
		return Optional.ofNullable(veiculos.get(placa));
	}

	@Override
	public List<Veiculo> buscarDisponiveis(String cidade, CategoriaCodigo categoria) {
		return veiculos.values().stream()
				.filter(veiculo -> veiculo.getCidade().equalsIgnoreCase(cidade)
						&& veiculo.getCategoria().equals(categoria) && veiculo.disponivel())
				.toList();
	}

	@Override
	public List<Veiculo> buscarDisponiveis(String cidade) {
		return veiculos.values().stream()
				.filter(veiculo -> veiculo.getCidade().equalsIgnoreCase(cidade) && veiculo.disponivel()).toList();
	}

	@Override
	public void salvar(Reserva reserva) {
		reservas.put(reserva.getCodigo(), reserva);
		var documento = reserva.getCliente().getCpfOuCnpj();
		clientes.put(documento, reserva.getCliente());
	}

	@Override
	public Optional<Reserva> buscarPorCodigo(String codigo) {
		return Optional.ofNullable(reservas.get(codigo));
	}

	@Override
	public List<Reserva> listar() {
		return new ArrayList<>(reservas.values());
	}

	@Override
	public void salvar(Locacao locacao) {
		locacoes.put(locacao.getCodigo(), locacao);
	}

	@Override
	public Optional<Locacao> buscarPorCodigoLocacao(String codigo) {
		return Optional.ofNullable(locacoes.get(codigo));
	}

	@Override
	public List<Locacao> listarLocacoes() {
		return new ArrayList<>(locacoes.values());
	}

	public void limpar() {
		categorias.clear();
		veiculos.clear();
		reservas.clear();
		locacoes.clear();
		clientes.clear();
	}

	@Override
	public void salvar(Cliente cliente) {
		clientes.put(cliente.getCpfOuCnpj(), cliente);
	}

	@Override
	public Optional<Cliente> buscarPorDocumento(String cpfOuCnpj) {
		return Optional.ofNullable(clientes.get(cpfOuCnpj));
	}

	@Override
	public List<Cliente> listarClientes() {
		return new ArrayList<>(clientes.values());
	}
}
