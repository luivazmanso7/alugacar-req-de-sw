package dev.sauloaraujo.sgb.apresentacao.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Handler global de exceções para a API REST.
 * Captura exceções e retorna respostas HTTP adequadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Trata exceções de argumentos ilegais (400 Bad Request).
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
			IllegalArgumentException ex, WebRequest request) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * Trata exceções de estado ilegal (409 Conflict).
	 */
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalStateException(
			IllegalStateException ex, WebRequest request) {
		return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
	}

	/**
	 * Trata exceções genéricas (500 Internal Server Error).
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGlobalException(
			Exception ex, WebRequest request) {
		return buildErrorResponse(
				"Ocorreu um erro interno no servidor", 
				HttpStatus.INTERNAL_SERVER_ERROR, 
				request);
	}

	/**
	 * Constrói a resposta de erro padronizada.
	 */
	private ResponseEntity<Map<String, Object>> buildErrorResponse(
			String message, HttpStatus status, WebRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		body.put("path", request.getDescription(false).replace("uri=", ""));

		return new ResponseEntity<>(body, status);
	}
}
