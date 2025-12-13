package dev.sauloaraujo.sgb.aplicacao.locacao.admin;

import static org.apache.commons.lang3.Validate.notNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.admin.Administrador;
import dev.sauloaraujo.sgb.dominio.locacao.admin.AdministradorRepositorio;

@Service
public class AutenticacaoAdminServicoAplicacao {
    
    private final AdministradorRepositorio administradorRepositorio;
    
    public AutenticacaoAdminServicoAplicacao(AdministradorRepositorio administradorRepositorio) {
        this.administradorRepositorio = notNull(administradorRepositorio, "Repositório de administrador é obrigatório");
    }
    
    @Transactional(readOnly = true)
    public Administrador autenticar(LoginAdminCmd comando) {
        var administrador = administradorRepositorio.buscarPorLogin(comando.login())
                .orElseThrow(() -> new IllegalArgumentException("Login ou senha inválidos"));
        
        if (!administrador.autenticar(comando.login(), comando.senha())) {
            throw new IllegalArgumentException("Login ou senha inválidos");
        }
        
        return administrador;
    }
}

