package dev.sauloaraujo.sgb.apresentacao.locacao.autenticacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao.AutenticacaoServicoAplicacao;
import dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao.LoginCmd;
import dev.sauloaraujo.sgb.aplicacao.locacao.autenticacao.LoginResponse;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Controller REST para autenticação de clientes.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Operações de autenticação de clientes")
public class AutenticacaoController {
    
    private static final String SESSION_CLIENTE = "clienteAutenticado";
    
    private final AutenticacaoServicoAplicacao autenticacaoServico;
    
    public AutenticacaoController(AutenticacaoServicoAplicacao autenticacaoServico) {
        this.autenticacaoServico = autenticacaoServico;
    }
    
    @PostMapping("/login")
    @Operation(summary = "Fazer login",
               description = "Autentica um cliente e cria uma sessão")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        var comando = new LoginCmd(request.login(), request.senha());
        Cliente cliente = autenticacaoServico.autenticar(comando);
        
        // Armazenar cliente na sessão HTTP
        session.setAttribute(SESSION_CLIENTE, cliente);
        
        var response = new LoginResponse(
                cliente.getNome(),
                cliente.getCpfOuCnpj(),
                cliente.getEmail()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @org.springframework.web.bind.annotation.RequestMapping(value = "/login", method = org.springframework.web.bind.annotation.RequestMethod.OPTIONS)
    public ResponseEntity<Void> loginOptions(HttpServletResponse response) {
        // Adicionar headers CORS manualmente se necessário
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Fazer logout",
               description = "Encerra a sessão do cliente")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
    
    /**
     * DTO de request para login.
     */
    record LoginRequest(
            @NotBlank(message = "Login é obrigatório")
            String login,
            
            @NotBlank(message = "Senha é obrigatória")
            String senha
    ) {}
}

