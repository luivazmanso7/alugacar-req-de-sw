package dev.sauloaraujo.sgb.apresentacao.locacao.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.admin.AutenticacaoAdminServicoAplicacao;
import dev.sauloaraujo.sgb.aplicacao.locacao.admin.LoginAdminCmd;
import dev.sauloaraujo.sgb.aplicacao.locacao.admin.LoginAdminResponse;
import dev.sauloaraujo.sgb.dominio.locacao.admin.Administrador;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/admin/auth")
@Tag(name = "Autenticação Admin", description = "Operações de autenticação de administradores")
public class AutenticacaoAdminController {
    
    private static final String SESSION_ADMIN = "administradorAutenticado";
    
    private final AutenticacaoAdminServicoAplicacao autenticacaoServico;
    
    public AutenticacaoAdminController(AutenticacaoAdminServicoAplicacao autenticacaoServico) {
        this.autenticacaoServico = autenticacaoServico;
    }
    
    @PostMapping("/login")
    @Operation(summary = "Fazer login como administrador",
               description = "Autentica um administrador e cria uma sessão")
    public ResponseEntity<LoginAdminResponse> login(
            @Valid @RequestBody LoginAdminRequest request,
            HttpSession session) {
        var comando = new LoginAdminCmd(request.login(), request.senha());
        Administrador administrador = autenticacaoServico.autenticar(comando);
        
        session.setAttribute(SESSION_ADMIN, administrador);
        session.setMaxInactiveInterval(60 * 60 * 8);
        
        var response = new LoginAdminResponse(
                administrador.getNome(),
                administrador.getEmail()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Fazer logout",
               description = "Encerra a sessão do administrador")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
    
    record LoginAdminRequest(
            @NotBlank(message = "Login é obrigatório")
            String login,
            
            @NotBlank(message = "Senha é obrigatória")
            String senha
    ) {}
}

