package dev.sauloaraujo.sgb.apresentacao.locacao.reserva;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.CriarReservaCmd;
import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ReservaServicoAplicacao;
import dev.sauloaraujo.sgb.dominio.locacao.catalogo.CategoriaCodigo;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/reservas")
@Tag(name = "Reservas", description = "Operações de criação e consulta de reservas")
public class CriarReservaController {

    private final ReservaServicoAplicacao servico;

    public CriarReservaController(ReservaServicoAplicacao servico) {
        this.servico = servico;
    }

    @PostMapping
    @Operation(summary = "Criar nova reserva",
               description = "Cria uma nova reserva de veículo com validação de disponibilidade e cálculo de valor estimado")
    public ResponseEntity<ReservaResponse> criar(@Valid @RequestBody CriarReservaRequest request) {

        // 1. Converter Request (JSON) -> Command (Domínio)
        var periodo = new PeriodoLocacao(
                request.periodo().dataRetirada(),
                request.periodo().dataDevolucao()
        );

        // Criar cliente (o serviço de domínio registra se necessário)
        var cliente = new Cliente(
                request.cliente().nome(),
                request.cliente().cpfOuCnpj(),
                request.cliente().cnh(),
                request.cliente().email(),
                request.cliente().login(),
                request.cliente().senha()
        );

        var comando = new CriarReservaCmd(
                CategoriaCodigo.valueOf(request.categoriaCodigo()),
                request.cidadeRetirada(),
                periodo,
                cliente
        );

        // 2. Chamar Aplicação
        var resumo = servico.criar(comando);

        // 3. Retornar Resposta
        return ResponseEntity.status(201).body(new ReservaResponse(
                resumo.codigo(),
                resumo.categoria(),
                resumo.cidadeRetirada(),
                resumo.dataRetirada(),
                resumo.dataDevolucao(),
                resumo.valorEstimado(),
                resumo.status(),
                resumo.clienteNome(),
                resumo.clienteDocumento()
        ));
    }
}

// DTOs de Request e Response

record CriarReservaRequest(
        @NotBlank(message = "Categoria é obrigatória")
        String categoriaCodigo,

        @NotBlank(message = "Cidade de retirada é obrigatória")
        String cidadeRetirada,

        @NotNull(message = "Período é obrigatório")
        @Valid
        PeriodoDto periodo,

        @NotNull(message = "Cliente é obrigatório")
        @Valid
        ClienteDto cliente
) {}

record PeriodoDto(
        @NotNull(message = "Data de retirada é obrigatória")
        LocalDateTime dataRetirada,

        @NotNull(message = "Data de devolução é obrigatória")
        LocalDateTime dataDevolucao
) {}

record ClienteDto(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "CPF/CNPJ é obrigatório")
        String cpfOuCnpj,

        @NotBlank(message = "CNH é obrigatória")
        String cnh,

        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @NotBlank(message = "Login é obrigatório")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        String senha
) {}

record ReservaResponse(
        String codigo,
        String categoria,
        String cidadeRetirada,
        LocalDateTime dataRetirada,
        LocalDateTime dataDevolucao,
        java.math.BigDecimal valorEstimado,
        String status,
        String clienteNome,
        String clienteDocumento
) {}

