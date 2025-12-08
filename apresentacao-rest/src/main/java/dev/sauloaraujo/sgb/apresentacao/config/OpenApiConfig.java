package dev.sauloaraujo.sgb.apresentacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuração do OpenAPI (Swagger) para documentação da API REST.
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("API - Sistema de Locação de Veículos")
						.version("1.0.0")
						.description("API REST para gerenciamento de locação de veículos seguindo Clean Architecture e DDD")
						.contact(new Contact()
								.name("Equipe AlugaCar")
								.email("contato@alugacar.com.br"))
						.license(new License()
								.name("MIT License")
								.url("https://opensource.org/licenses/MIT")));
	}
}
