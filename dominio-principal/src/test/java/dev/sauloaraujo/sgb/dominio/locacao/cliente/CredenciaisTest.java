package dev.sauloaraujo.sgb.dominio.locacao.cliente;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.sauloaraujo.sgb.dominio.locacao.shared.Credenciais;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Credenciais - Value Object")
class CredenciaisTest {
	
	@Test
	@DisplayName("Deve criar credenciais válidas")
	void deveCriarCredenciaisValidas() {
		var credenciais = Credenciais.criar("joao.silva", "senha123");
		
		assertNotNull(credenciais);
		assertEquals("joao.silva", credenciais.getLogin());
		assertNotNull(credenciais.getSenhaCriptografada());
	}
	
	@Test
	@DisplayName("Deve validar senha corretamente")
	void deveValidarSenhaCorretamente() {
		var credenciais = Credenciais.criar("maria.santos", "senha456");
		
		assertTrue(credenciais.verificarSenha("senha456"));
		assertFalse(credenciais.verificarSenha("senhaErrada"));
		assertFalse(credenciais.verificarSenha(""));
		assertFalse(credenciais.verificarSenha(null));
	}
	
	@Test
	@DisplayName("Deve rejeitar login inválido - muito curto")
	void deveRejeitarLoginMuitoCurto() {
		var exception = assertThrows(IllegalArgumentException.class, 
			() -> Credenciais.criar("abc", "senha123"));
		
		assertTrue(exception.getMessage().contains("4-30 caracteres"));
	}
	
	@Test
	@DisplayName("Deve rejeitar login inválido - muito longo")
	void deveRejeitarLoginMuitoLongo() {
		var loginLongo = "a".repeat(31);
		var exception = assertThrows(IllegalArgumentException.class, 
			() -> Credenciais.criar(loginLongo, "senha123"));
		
		assertTrue(exception.getMessage().contains("4-30 caracteres"));
	}
	
	@Test
	@DisplayName("Deve rejeitar login inválido - caracteres especiais")
	void deveRejeitarLoginCaracteresEspeciais() {
		var exception = assertThrows(IllegalArgumentException.class, 
			() -> Credenciais.criar("usuario@teste", "senha123"));
		
		assertTrue(exception.getMessage().contains("Login inválido"));
	}
	
	@Test
	@DisplayName("Deve aceitar login com pontos, hífens e underscores")
	void deveAceitarLoginComCaracteresPermitidos() {
		assertDoesNotThrow(() -> Credenciais.criar("joao.silva", "senha123"));
		assertDoesNotThrow(() -> Credenciais.criar("joao-silva", "senha123"));
		assertDoesNotThrow(() -> Credenciais.criar("joao_silva", "senha123"));
		assertDoesNotThrow(() -> Credenciais.criar("joao.silva-123", "senha123"));
	}
	
	@Test
	@DisplayName("Deve rejeitar senha muito curta")
	void deveRejeitarSenhaMuitoCurta() {
		var exception = assertThrows(IllegalArgumentException.class, 
			() -> Credenciais.criar("joao.silva", "12345"));
		
		assertTrue(exception.getMessage().contains("mínimo 6 caracteres"));
	}
	
	@Test
	@DisplayName("Deve rejeitar login nulo")
	void deveRejeitarLoginNulo() {
		assertThrows(NullPointerException.class, 
			() -> Credenciais.criar(null, "senha123"));
	}
	
	@Test
	@DisplayName("Deve rejeitar senha nula")
	void deveRejeitarSenhaNula() {
		assertThrows(NullPointerException.class, 
			() -> Credenciais.criar("joao.silva", null));
	}
	
	@Test
	@DisplayName("Deve comparar credenciais por igualdade")
	void deveCompararCredenciaisPorIgualdade() {
		var cred1 = Credenciais.criar("joao.silva", "senha123");
		var cred2 = Credenciais.criar("joao.silva", "senha123");
		
		assertEquals(cred1, cred2);
		assertEquals(cred1.hashCode(), cred2.hashCode());
	}
	
	@Test
	@DisplayName("Deve retornar toString sem expor senha")
	void deveRetornarToStringSemExporSenha() {
		var credenciais = Credenciais.criar("joao.silva", "senha123");
		var toString = credenciais.toString();
		
		assertTrue(toString.contains("joao.silva"));
		assertTrue(toString.contains("***"));
		assertFalse(toString.contains("senha123"));
	}
}
