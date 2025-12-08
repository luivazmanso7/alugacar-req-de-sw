package dev.sauloaraujo.sgb.aplicacao.locacao.categoria;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface de Resumo para Categoria.
 * Representa uma projeção simplificada dos dados de Categoria para a camada de aplicação.
 */
public interface CategoriaResumo {
	String getCodigo();
	
	String getNome();
	
	String getDescricao();
	
	BigDecimal getDiaria();
	
	List<String> getModelosExemplo();
	
	int getQuantidadeDisponivel();
}
