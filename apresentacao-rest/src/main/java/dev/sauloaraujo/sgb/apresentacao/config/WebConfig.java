package dev.sauloaraujo.sgb.apresentacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.DispatcherServlet;

import dev.sauloaraujo.sgb.apresentacao.locacao.autenticacao.AutenticacaoInterceptor;

/**
 * Configuração do Spring MVC para registrar interceptors e configurar tratamento de erros.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AutenticacaoInterceptor autenticacaoInterceptor;

    public WebConfig(AutenticacaoInterceptor autenticacaoInterceptor) {
        this.autenticacaoInterceptor = autenticacaoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacaoInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/login",
                    "/veiculos/**",
                    "/categorias/**",
                    "/swagger-ui/**",
                    "/api-docs/**"
                );
    }
}

