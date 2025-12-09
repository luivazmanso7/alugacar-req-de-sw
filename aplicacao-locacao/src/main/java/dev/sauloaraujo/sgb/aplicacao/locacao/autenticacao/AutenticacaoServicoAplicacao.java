package dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao;

import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço de aplicação responsável pela autenticação de clientes.
 * Segue padrão Clean Architecture.
 */
@Service
public class AutenticacaoServicoAplicacao {
	
	@Autowired
	private ClienteRepositorio clienteRepositorio;
	
	/**
	 * Autentica um cliente pelo login e senha.
	 * 
	 * @param login Login do cliente
	 * @param senha Senha em texto plano
	 * @return Cliente autenticado, se credenciais forem válidas
	 * @throws AutenticacaoException se credenciais inválidas ou cliente bloqueado
	 */
	public Cliente autenticar(String login, String senha) {
		var clienteOpt = clienteRepositorio.buscarPorLogin(login);
		
		if (clienteOpt.isEmpty()) {
			throw new AutenticacaoException("Credenciais inválidas");
		}
		
		var cliente = clienteOpt.get();
		
		try {
			if (!cliente.autenticar(login, senha)) {
				throw new AutenticacaoException("Credenciais inválidas");
			}
		} catch (IllegalStateException e) {
			// Cliente bloqueado ou inativo
			throw new AutenticacaoException(e.getMessage());
		}
		
		return cliente;
	}
	
	/**
	 * Busca um cliente por login (sem autenticação).
	 * 
	 * @param login Login do cliente
	 * @return Cliente, se encontrado
	 */
	public Optional<Cliente> buscarPorLogin(String login) {
		return clienteRepositorio.buscarPorLogin(login);
	}
	
	/**
	 * Busca um cliente por documento (CPF/CNPJ).
	 * 
	 * @param documento Documento do cliente
	 * @return Cliente, se encontrado
	 */
	public Optional<Cliente> buscarPorDocumento(String documento) {
		return clienteRepositorio.buscarPorDocumento(documento);
	}
	
	/**
	 * Cadastra um novo cliente no sistema.
	 * 
	 * @param nome Nome completo
	 * @param cpfOuCnpj CPF ou CNPJ
	 * @param cnh Número da CNH
	 * @param email E-mail
	 * @param login Login para acesso
	 * @param senha Senha em texto plano
	 * @return Cliente cadastrado
	 */
	public Cliente cadastrarCliente(String nome, String cpfOuCnpj, String cnh, 
	                                String email, String login, String senha) {
		// Verificar se já existe cliente com mesmo CPF
		if (clienteRepositorio.buscarPorDocumento(cpfOuCnpj).isPresent()) {
			throw new IllegalArgumentException("Já existe cliente com este CPF/CNPJ");
		}
		
		// Verificar se já existe cliente com mesmo login
		if (clienteRepositorio.buscarPorLogin(login).isPresent()) {
			throw new IllegalArgumentException("Login já está em uso");
		}
		
		var cliente = new Cliente(nome, cpfOuCnpj, cnh, email, login, senha);
		clienteRepositorio.salvar(cliente);
		
		return cliente;
	}
}
