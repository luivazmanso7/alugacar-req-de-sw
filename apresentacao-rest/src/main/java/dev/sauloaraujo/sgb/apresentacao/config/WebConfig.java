package dev.sauloaraujo.sgb.apresentacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.DispatcherServlet;

import dev.sauloaraujo.sgb.apresentacao.locacao.admin.AdminInterceptor;
import dev.sauloaraujo.sgb.apresentacao.locacao.autenticacao.AutenticacaoInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AutenticacaoInterceptor autenticacaoInterceptor;
    private final AdminInterceptor adminInterceptor;

    public WebConfig(AutenticacaoInterceptor autenticacaoInterceptor, AdminInterceptor adminInterceptor) {
        this.autenticacaoInterceptor = autenticacaoInterceptor;
        this.adminInterceptor = adminInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacaoInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/login",
                    "/admin/**",
                    "/veiculos/**",
                    "/categorias/**",
                    "/swagger-ui/**",
                    "/api-docs/**"
                );
        
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/auth/login");
    }
}

