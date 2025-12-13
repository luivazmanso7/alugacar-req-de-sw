package dev.sauloaraujo.sgb.apresentacao.locacao.autenticacao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;

/**
 * Interceptor para verificar autenticação em rotas protegidas.
 * Usa sessão HTTP simples.
 */
@Component
public class AutenticacaoInterceptor implements HandlerInterceptor {
    
    private static final String SESSION_CLIENTE = "clienteAutenticado";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String path = request.getRequestURI();
        
        // Rotas públicas (não precisam autenticação)
        if (path.startsWith("/auth/login") || 
            path.startsWith("/veiculos") ||
            path.startsWith("/categorias") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/api-docs") ||
            path.equals("/auth/logout")) {
            return true;
        }
        
        // Verificar sessão
        HttpSession session = request.getSession(false);
        
        // Permitir acesso se for admin autenticado (admin tem acesso a tudo)
        if (session != null) {
            Object admin = session.getAttribute("administradorAutenticado");
            if (admin != null) {
                return true; // Admin tem acesso a todas as rotas
            }
        }
        
        if (path.startsWith("/reservas")) {
            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Sessão não encontrada. Faça login primeiro.\"}");
                return false;
            }
            
            Cliente cliente = (Cliente) session.getAttribute(SESSION_CLIENTE);
            if (cliente == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Cliente não autenticado. Faça login primeiro.\"}");
                return false;
            }
            
            request.setAttribute("clienteAutenticado", cliente);
            return true;
        }
        
        // Para outras rotas protegidas
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Sessão não encontrada. Faça login primeiro.\"}");
            return false;
        }
        
        Cliente cliente = (Cliente) session.getAttribute(SESSION_CLIENTE);
        if (cliente == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Cliente não autenticado. Faça login primeiro.\"}");
            return false;
        }
        
        request.setAttribute("clienteAutenticado", cliente);
        return true;
    }
}

