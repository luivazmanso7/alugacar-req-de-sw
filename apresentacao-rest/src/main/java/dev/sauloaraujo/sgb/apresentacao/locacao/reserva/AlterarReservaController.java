package dev.sauloaraujo.sgb.apresentacao.locacao.reserva;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.AlterarReservaCmd;
import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ReservaServicoAplicacao;
import dev.sauloaraujo.sgb.dominio.locacao.shared.PeriodoLocacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/reservas")
@Tag(name = "Reservas", description = "Operações de alteração de reservas")
public class AlterarReservaController {

    private final ReservaServicoAplicacao servico;

    public AlterarReservaController(ReservaServicoAplicacao servico) {
        this.servico = servico;
    }

    @PatchMapping("/{codigoReserva}/periodo")
    @Operation(summary = "Alterar período de reserva",
               description = "Altera o período de uma reserva ativa, recalculando o valor estimado")
    public ResponseEntity<ReservaResponse> alterarPeriodo(
            @PathVariable String codigoReserva,
            @Valid @RequestBody AlterarPeriodoRequest request) {

        // 1. Converter Request (JSON) -> Command (Aplicação)
        var novoPeriodo = new PeriodoLocacao(
                request.dataRetirada(),
                request.dataDevolucao()
        );

        var comando = new AlterarReservaCmd(codigoReserva, novoPeriodo);

        // 2. Chamar Aplicação
        var resumo = servico.alterar(comando);

        // 3. Retornar Resposta (reutiliza ReservaResponse do CriarReservaController)
        return ResponseEntity.ok(new dev.sauloaraujo.sgb.apresentacao.locacao.reserva.ReservaResponse(
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

// DTOs de Request

record AlterarPeriodoRequest(
        @NotNull(message = "Data de retirada é obrigatória")
        LocalDateTime dataRetirada,

        @NotNull(message = "Data de devolução é obrigatória")
        LocalDateTime dataDevolucao
) {}

