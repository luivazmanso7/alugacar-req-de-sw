package dev.sauloaraujo.sgb.apresentacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dev.sauloaraujo.sgb.apresentacao.locacao.autenticacao.AutenticacaoInterceptor;

/**
 * Configuração do Spring MVC para registrar interceptors.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final AutenticacaoInterceptor autenticacaoInterceptor;
    
    public WebConfig(AutenticacaoInterceptor autenticacaoInterceptor) {
        this.autenticacaoInterceptor = autenticacaoInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("🔧 Registrando AutenticacaoInterceptor...");
        // IMPORTANTE: Como o context path é /api/v1, os path patterns são relativos ao context path
        // Então /api/v1/reservas vira apenas /reservas no interceptor
        registry.addInterceptor(autenticacaoInterceptor)
                .addPathPatterns("/**")  // Intercepta todas as rotas dentro do context path
                .excludePathPatterns(
                    "/auth/login",        // Relativo ao context path
                    "/veiculos/**",       // Relativo ao context path
                    "/categorias/**",     // Relativo ao context path
                    "/swagger-ui/**",     // Relativo ao context path
                    "/api-docs/**"        // Relativo ao context path
                );
        System.out.println("✅ AutenticacaoInterceptor registrado com sucesso!");
    }
}

