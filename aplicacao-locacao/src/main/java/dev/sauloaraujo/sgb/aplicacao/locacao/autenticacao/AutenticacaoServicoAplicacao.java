package dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao;

import static org.apache.commons.lang3.Validate.notNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.ClienteRepositorio;

/**
 * Serviço de aplicação para autenticação de clientes.
 */
@Service
public class AutenticacaoServicoAplicacao {
    
    private final ClienteRepositorio clienteRepositorio;
    
    public AutenticacaoServicoAplicacao(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = notNull(clienteRepositorio, "Repositório de cliente é obrigatório");
    }
    
    /**
     * Autentica um cliente.
     * 
     * @param comando comando contendo login e senha
     * @return dados do cliente autenticado
     * @throws IllegalArgumentException se as credenciais são inválidas
     * @throws IllegalStateException se o cliente está bloqueado ou inativo
     */
    @Transactional(readOnly = true)
    public Cliente autenticar(LoginCmd comando) {
        // Buscar cliente por login
        var cliente = clienteRepositorio.buscarPorLogin(comando.login())
                .orElseThrow(() -> new IllegalArgumentException("Login ou senha inválidos"));
        
        if (!cliente.autenticar(comando.login(), comando.senha())) {
            throw new IllegalArgumentException("Login ou senha inválidos");
        }
        
        return cliente;
    }
}

