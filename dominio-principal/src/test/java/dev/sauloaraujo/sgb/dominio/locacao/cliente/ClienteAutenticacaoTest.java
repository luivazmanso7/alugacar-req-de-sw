package dev.sauloaraujo.sgb.dominio.locacao.cliente;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cliente - Autenticação e Status")
class ClienteAutenticacaoTest {
	
	@Test
	@DisplayName("Deve criar cliente com credenciais e status ATIVO")
	void deveCriarClienteComCredenciais() {
		var cliente = new Cliente(
			"João Silva", 
			"12345678901", 
			"12345678901", 
			"joao@email.com",
			"joao.silva",
			"senha123"
		);
		
		assertNotNull(cliente);
		assertEquals("João Silva", cliente.getNome());
		assertEquals("joao.silva", cliente.getCredenciais().getLogin());
		assertEquals(StatusCliente.ATIVO, cliente.getStatus());
	}
	
	@Test
	@DisplayName("Deve autenticar cliente com credenciais corretas")
	void deveAutenticarClienteComCredenciaisCorretas() {
		var cliente = new Cliente(
			"Maria Santos", 
			"98765432100", 
			"98765432100", 
			"maria@email.com",
			"maria.santos",
			"senha456"
		);
		
		assertTrue(cliente.autenticar("maria.santos", "senha456"));
	}
	
	@Test
	@DisplayName("Deve rejeitar autenticação com senha incorreta")
	void deveRejeitarAutenticacaoComSenhaIncorreta() {
		var cliente = new Cliente(
			"Carlos Oliveira", 
			"45678912300", 
			"45678912300", 
			"carlos@email.com",
			"carlos.oliveira",
			"senha789"
		);
		
		assertFalse(cliente.autenticar("carlos.oliveira", "senhaErrada"));
	}
	
	@Test
	@DisplayName("Deve rejeitar autenticação com login incorreto")
	void deveRejeitarAutenticacaoComLoginIncorreto() {
		var cliente = new Cliente(
			"Ana Paula", 
			"11122233344", 
			"11122233344", 
			"ana@email.com",
			"ana.paula",
			"senha000"
		);
		
		assertFalse(cliente.autenticar("ana.silva", "senha000"));
	}
	
	@Test
	@DisplayName("Deve bloquear cliente")
	void deveBloqueiarCliente() {
		var cliente = new Cliente(
			"João Silva", 
			"12345678901", 
			"12345678901", 
			"joao@email.com",
			"joao.silva",
			"senha123"
		);
		
		cliente.bloquear();
		
		assertEquals(StatusCliente.BLOQUEADO, cliente.getStatus());
	}
	
	@Test
	@DisplayName("Deve rejeitar autenticação de cliente bloqueado")
	void deveRejeitarAutenticacaoClienteBloqueado() {
		var cliente = new Cliente(
			"Maria Santos", 
			"98765432100", 
			"98765432100", 
			"maria@email.com",
			"maria.santos",
			"senha456"
		);
		
		cliente.bloquear();
		
		var exception = assertThrows(IllegalStateException.class, 
			() -> cliente.autenticar("maria.santos", "senha456"));
		
		assertTrue(exception.getMessage().contains("bloqueado"));
	}
	
	@Test
	@DisplayName("Deve desbloquear cliente")
	void deveDesbloquearCliente() {
		var cliente = new Cliente(
			"Carlos Oliveira", 
			"45678912300", 
			"45678912300", 
			"carlos@email.com",
			"carlos.oliveira",
			"senha789"
		);
		
		cliente.bloquear();
		cliente.desbloquear();
		
		assertEquals(StatusCliente.ATIVO, cliente.getStatus());
		assertTrue(cliente.autenticar("carlos.oliveira", "senha789"));
	}
	
	@Test
	@DisplayName("Deve inativar cliente")
	void deveInativarCliente() {
		var cliente = new Cliente(
			"Ana Paula", 
			"11122233344", 
			"11122233344", 
			"ana@email.com",
			"ana.paula",
			"senha000"
		);
		
		cliente.inativar();
		
		assertEquals(StatusCliente.INATIVO, cliente.getStatus());
	}
	
	@Test
	@DisplayName("Deve rejeitar autenticação de cliente inativo")
	void deveRejeitarAutenticacaoClienteInativo() {
		var cliente = new Cliente(
			"João Silva", 
			"12345678901", 
			"12345678901", 
			"joao@email.com",
			"joao.silva",
			"senha123"
		);
		
		cliente.inativar();
		
		var exception = assertThrows(IllegalStateException.class, 
			() -> cliente.autenticar("joao.silva", "senha123"));
		
		assertTrue(exception.getMessage().contains("inativo"));
	}
	
	@Test
	@DisplayName("Não deve permitir bloquear cliente inativo")
	void naoDevePermitirBloquearClienteInativo() {
		var cliente = new Cliente(
			"Maria Santos", 
			"98765432100", 
			"98765432100", 
			"maria@email.com",
			"maria.santos",
			"senha456"
		);
		
		cliente.inativar();
		
		var exception = assertThrows(IllegalStateException.class, 
			() -> cliente.bloquear());
		
		assertTrue(exception.getMessage().contains("inativo"));
	}
	
	@Test
	@DisplayName("Não deve permitir desbloquear cliente ativo")
	void naoDevePermitirDesbloquearClienteAtivo() {
		var cliente = new Cliente(
			"Carlos Oliveira", 
			"45678912300", 
			"45678912300", 
			"carlos@email.com",
			"carlos.oliveira",
			"senha789"
		);
		
		var exception = assertThrows(IllegalStateException.class, 
			() -> cliente.desbloquear());
		
		assertTrue(exception.getMessage().contains("não está bloqueado"));
	}
}
