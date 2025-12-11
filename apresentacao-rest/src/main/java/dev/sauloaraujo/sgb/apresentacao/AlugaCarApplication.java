package dev.sauloaraujo.sgb.apresentacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "dev.sauloaraujo.sgb")
@EnableJpaRepositories(basePackages = "dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.repository")
@EntityScan(basePackages = "dev.sauloaraujo.sgb.infraestrutura.persistencia.jpa.entities")
public class AlugaCarApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlugaCarApplication.class, args);
    }
}
