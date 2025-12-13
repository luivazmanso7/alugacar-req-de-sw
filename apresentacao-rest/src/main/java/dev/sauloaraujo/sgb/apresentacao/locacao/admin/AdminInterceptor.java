package dev.sauloaraujo.sgb.apresentacao.locacao.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import dev.sauloaraujo.sgb.dominio.locacao.admin.Administrador;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    
    private static final String SESSION_ADMIN = "administradorAutenticado";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String path = request.getRequestURI();
        
        if (path.startsWith("/admin/auth/login")) {
            return true;
        }
        
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Sessão não encontrada. Faça login primeiro.\"}");
            return false;
        }
        
        Administrador administrador = (Administrador) session.getAttribute(SESSION_ADMIN);
        
        if (administrador == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Administrador não autenticado. Faça login primeiro.\"}");
            return false;
        }
        
        request.setAttribute("administradorAutenticado", administrador);
        return true;
    }
}

