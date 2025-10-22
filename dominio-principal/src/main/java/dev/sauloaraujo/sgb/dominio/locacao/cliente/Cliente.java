package dev.sauloaraujo.sgb.dominio.locacao.cliente;

import java.util.Objects;
import java.util.regex.Pattern;

public class Cliente {
	private static final Pattern CPF_FORMATO = Pattern.compile("\\d{11}");
	private static final Pattern CNH_FORMATO = Pattern.compile("\\d{11}");

	private final String nome;
	private final String cpfOuCnpj;
	private final String cnh;
	private final String email;

	public Cliente(String nome, String cpfOuCnpj, String cnh, String email) {
		this.nome = validarNome(nome);
		this.cpfOuCnpj = validarDocumento(cpfOuCnpj);
		this.cnh = validarCnh(cnh);
		this.email = Objects.requireNonNull(email, "O e-mail do cliente é obrigatório");
	}

	public String getNome() {
		return nome;
	}

	public String getCpfOuCnpj() {
		return cpfOuCnpj;
	}

	public String getCnh() {
		return cnh;
	}

	public String getEmail() {
		return email;
	}

	private String validarNome(String nome) {
		var valor = Objects.requireNonNull(nome, "O nome do cliente é obrigatório").trim();
		if (valor.isEmpty()) {
			throw new IllegalArgumentException("O nome do cliente é obrigatório");
		}
		return valor;
	}

	private String validarDocumento(String documento) {
		var valor = Objects.requireNonNull(documento, "O documento do cliente é obrigatório").replaceAll("\\D", "");
		if (!CPF_FORMATO.matcher(valor).matches()) {
			throw new IllegalArgumentException("CPF/CNPJ inválido. Utilize somente números");
		}
		return valor;
	}

	private String validarCnh(String cnh) {
		var valor = Objects.requireNonNull(cnh, "A CNH é obrigatória").replaceAll("\\D", "");
		if (!CNH_FORMATO.matcher(valor).matches()) {
			throw new IllegalArgumentException("CNH inválida. Utilize somente números");
		}
		return valor;
	}
}
