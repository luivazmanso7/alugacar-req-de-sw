package dev.sauloaraujo.sgb.dominio.locacao.operacao;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para as estratégias de cálculo de multa.
 * 
 * <p>
 * Demonstra o uso do padrão Strategy para diferentes políticas de multa.
 * </p>
 */
@DisplayName("Testes do Padrão Strategy - Cálculo de Multas")
class CalculoMultaStrategyTest {

	@Test
	@DisplayName("Deve calcular multa padrão corretamente")
	void deveCalcularMultaPadraoCorretamente() {
		// Arrange
		CalculoMultaStrategy strategy = new MultaPadraoStrategy();
		BigDecimal valorAtraso = new BigDecimal("300.00");
		BigDecimal percentual = new BigDecimal("0.20"); // 20%

		// Act
		BigDecimal multa = strategy.calcular(valorAtraso, percentual);

		// Assert
		assertEquals(0, new BigDecimal("60.00").compareTo(multa),
				"Multa de 20% sobre R$ 300 deve ser R$ 60");
	}

	@Test
	@DisplayName("Deve retornar zero quando valor do atraso for zero")
	void deveRetornarZeroQuandoValorAtrasoForZero() {
		// Arrange
		CalculoMultaStrategy strategy = new MultaPadraoStrategy();
		BigDecimal valorAtraso = BigDecimal.ZERO;
		BigDecimal percentual = new BigDecimal("0.20");

		// Act
		BigDecimal multa = strategy.calcular(valorAtraso, percentual);

		// Assert
		assertEquals(BigDecimal.ZERO, multa,
				"Multa deve ser zero quando não há atraso");
	}

	@Test
	@DisplayName("Deve retornar zero quando valor do atraso for negativo")
	void deveRetornarZeroQuandoValorAtrasoForNegativo() {
		// Arrange
		CalculoMultaStrategy strategy = new MultaPadraoStrategy();
		BigDecimal valorAtraso = new BigDecimal("-100.00");
		BigDecimal percentual = new BigDecimal("0.20");

		// Act
		BigDecimal multa = strategy.calcular(valorAtraso, percentual);

		// Assert
		assertEquals(BigDecimal.ZERO, multa,
				"Multa deve ser zero quando valor de atraso é negativo");
	}

	@Test
	@DisplayName("Deve retornar zero quando percentual for nulo")
	void deveRetornarZeroQuandoPercentualForNulo() {
		// Arrange
		CalculoMultaStrategy strategy = new MultaPadraoStrategy();
		BigDecimal valorAtraso = new BigDecimal("300.00");
		BigDecimal percentual = null;

		// Act
		BigDecimal multa = strategy.calcular(valorAtraso, percentual);

		// Assert
		assertEquals(BigDecimal.ZERO, multa,
				"Multa deve ser zero quando percentual é nulo");
	}

	@Test
	@DisplayName("Deve isentar multa para cliente VIP")
	void deveIsentarMultaParaClienteVip() {
		// Arrange
		CalculoMultaStrategy strategy = new MultaIsentaStrategy();
		BigDecimal valorAtraso = new BigDecimal("500.00");
		BigDecimal percentual = new BigDecimal("0.50"); // 50%

		// Act
		BigDecimal multa = strategy.calcular(valorAtraso, percentual);

		// Assert
		assertEquals(BigDecimal.ZERO, multa,
				"Multa deve ser zero para estratégia de isenção, independente do valor");
	}

	@Test
	@DisplayName("Deve sempre retornar zero para multa isenta mesmo com valores altos")
	void deveSempreRetornarZeroParaMultaIsenta() {
		// Arrange
		CalculoMultaStrategy strategy = new MultaIsentaStrategy();
		BigDecimal valorAtraso = new BigDecimal("10000.00");
		BigDecimal percentual = new BigDecimal("1.00"); // 100%

		// Act
		BigDecimal multa = strategy.calcular(valorAtraso, percentual);

		// Assert
		assertEquals(BigDecimal.ZERO, multa,
				"Multa isenta deve sempre retornar zero");
	}

	@Test
	@DisplayName("Deve calcular multa com percentual diferente")
	void deveCalcularMultaComPercentualDiferente() {
		// Arrange
		CalculoMultaStrategy strategy = new MultaPadraoStrategy();
		BigDecimal valorAtraso = new BigDecimal("200.00");
		BigDecimal percentual = new BigDecimal("0.10"); // 10%

		// Act
		BigDecimal multa = strategy.calcular(valorAtraso, percentual);

		// Assert
		assertEquals(0, new BigDecimal("20.00").compareTo(multa),
				"Multa de 10% sobre R$ 200 deve ser R$ 20");
	}

	@Test
	@DisplayName("Deve permitir substituir estratégia em tempo de execução")
	void devePermitirSubstituirEstrategia() {
		// Arrange
		BigDecimal valorAtraso = new BigDecimal("400.00");
		BigDecimal percentual = new BigDecimal("0.25"); // 25%

		// Act & Assert - Estratégia Padrão
		CalculoMultaStrategy estrategiaPadrao = new MultaPadraoStrategy();
		BigDecimal multaPadrao = estrategiaPadrao.calcular(valorAtraso, percentual);
		assertEquals(0, new BigDecimal("100.00").compareTo(multaPadrao),
				"Multa padrão de 25% sobre R$ 400 deve ser R$ 100");

		// Act & Assert - Estratégia Isenta
		CalculoMultaStrategy estrategiaIsenta = new MultaIsentaStrategy();
		BigDecimal multaIsenta = estrategiaIsenta.calcular(valorAtraso, percentual);
		assertEquals(BigDecimal.ZERO, multaIsenta,
				"Multa isenta deve ser zero");

		// Demonstra flexibilidade do padrão Strategy
		assertNotEquals(0, multaPadrao.compareTo(multaIsenta),
				"Diferentes estratégias devem produzir resultados diferentes");
	}
}
