package dev.sauloaraujo.sgb.apresentacao.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

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
	 * Trata exceções de recurso não encontrado (404 Not Found).
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(
			NoHandlerFoundException ex, WebRequest request) {
		return buildErrorResponse(
				"Recurso não encontrado: " + ex.getRequestURL(),
				HttpStatus.NOT_FOUND,
				request);
	}

	/**
	 * Trata exceções genéricas (500 Internal Server Error).
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGlobalException(
			Exception ex, WebRequest request) {
		ex.printStackTrace(); // Log do erro para debug
		return buildErrorResponse(
				ex.getMessage() != null ? ex.getMessage() : "Ocorreu um erro interno no servidor", 
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

		return ResponseEntity.status(status)
				.contentType(MediaType.APPLICATION_JSON)
				.body(body);
	}
}
