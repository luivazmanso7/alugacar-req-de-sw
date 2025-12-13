package dev.sauloaraujo.sgb.dominio.locacao.admin;

import java.util.Optional;

public interface AdministradorRepositorio {
	void salvar(Administrador administrador);
	Optional<Administrador> buscarPorId(String id);
	Optional<Administrador> buscarPorLogin(String login);
}

