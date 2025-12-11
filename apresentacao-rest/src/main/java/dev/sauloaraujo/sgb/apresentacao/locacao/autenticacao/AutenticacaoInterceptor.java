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
        // IMPORTANTE: Permitir requisições OPTIONS (preflight do CORS) sem autenticação
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("✅ Requisição OPTIONS (preflight CORS), permitindo acesso");
            return true;
        }
        
        // Log inicial para garantir que o interceptor está sendo chamado
        // IMPORTANTE: O Spring Boot está configurado com context path /api/v1
        // Então request.getRequestURI() retorna apenas o path relativo ao context path
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String fullPath = contextPath + servletPath + (path.startsWith("/") ? path : "/" + path);
        
        System.out.println("🔍 AutenticacaoInterceptor.preHandle chamado!");
        System.out.println("  RequestURI: " + path);
        System.out.println("  ContextPath: " + contextPath);
        System.out.println("  ServletPath: " + servletPath);
        System.out.println("  Full path: " + fullPath);
        
        // Rotas públicas que não precisam de autenticação
        // Como o context path é /api/v1, o path relativo será /auth/login, /veiculos, etc.
        if (path.startsWith("/auth/login") || 
            path.startsWith("/veiculos") ||
            path.startsWith("/categorias") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/api-docs") ||
            path.equals("/auth/logout")) {
            System.out.println("✅ Rota pública, permitindo acesso");
            return true;
        }
        
        // Log para debug
        System.out.println("=== AutenticacaoInterceptor ===");
        System.out.println("Path: " + path);
        System.out.println("Cookie header: " + request.getHeader("Cookie"));
        System.out.println("JSESSIONID cookie: " + getCookieValue(request, "JSESSIONID"));
        
        // Para rotas protegidas, verificar sessão
       // IMPORTANTE: getSession(false) não cria uma nova sessão se não existir
        // Mas se o cookie JSESSIONID está presente, devemos tentar encontrar a sessão
        HttpSession session = request.getSession(false);
        System.out.println("Session encontrada: " + (session != null));
        
        // Se não encontrou sessão mas há cookie JSESSIONID, pode ser problema de path/domain
        if (session == null) {
            String jsessionId = getCookieValue(request, "JSESSIONID");
            System.out.println("ERRO: Sessão não encontrada, mas cookie JSESSIONID presente: " + jsessionId);
            System.out.println("  Isso pode indicar que:");
            System.out.println("  1. A sessão expirou");
            System.out.println("  2. O cookie foi criado em um contexto diferente");
            System.out.println("  3. O path do cookie não corresponde");
            
            // Tentar criar uma nova sessão para debug (não é a solução ideal)
            HttpSession newSession = request.getSession(true);
            System.out.println("  Nova sessão criada com ID: " + newSession.getId());
            System.out.println("  Mas isso não resolve o problema - o cliente precisa fazer login novamente");
            
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Sessão não encontrada. Faça login primeiro.\"}");
            return false;
        }
        
        System.out.println("Session ID: " + session.getId());
        System.out.println("Session attributes: " + java.util.Collections.list(session.getAttributeNames()));
        
        Cliente cliente = (Cliente) session.getAttribute(SESSION_CLIENTE);
        System.out.println("Cliente na sessão: " + (cliente != null ? cliente.getNome() : "null"));
        
        if (cliente == null) {
            System.out.println("ERRO: Cliente não encontrado na sessão");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Cliente não autenticado. Faça login primeiro.\"}");
            return false;
        }
        
        // Armazenar cliente no request para uso no controller
        request.setAttribute("clienteAutenticado", cliente);
        System.out.println("✅ Autenticação OK");
        return true;
    }
    
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

