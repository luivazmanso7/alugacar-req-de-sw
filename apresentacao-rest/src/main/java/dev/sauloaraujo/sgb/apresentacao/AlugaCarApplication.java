package dev.sauloaraujo.sgb.apresentacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {
    "dev.sauloaraujo.sgb.apresentacao",     // Controllers e configs da camada REST
    "dev.sauloaraujo.sgb.aplicacao",        // Serviços da camada de aplicação
    "dev.sauloaraujo.sgb.infraestrutura",   // Repositórios e configs de infraestrutura
    "dev.sauloaraujo.sgb.dominio"           // Serviços de domínio
})
@EnableJpaRepositories(basePackages = "dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa")
@EntityScan(basePackages = "dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa")
public class AlugaCarApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlugaCarApplication.class, args);
    }
}
