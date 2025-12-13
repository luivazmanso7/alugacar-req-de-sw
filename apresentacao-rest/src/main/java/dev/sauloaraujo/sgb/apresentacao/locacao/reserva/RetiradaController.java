package dev.sauloaraujo.sgb.apresentacao.locacao.reserva;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.ConfirmarRetiradaCmd;
import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.ContratoResponse;
import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.RetiradaServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/reservas")
@Tag(name = "Retirada de Veículos", description = "Operações de confirmação de retirada e geração de contrato")
public class RetiradaController {

    private final RetiradaServicoAplicacao servico;

    public RetiradaController(RetiradaServicoAplicacao servico) {
        this.servico = servico;
    }

    @PostMapping("/{codigoReserva}/confirmar-retirada")
    @Operation(summary = "Confirmar retirada de veículo",
               description = "Confirma a retirada de um veículo, cria a locação e gera o contrato")
    public ResponseEntity<ContratoResponse> confirmarRetirada(
            @PathVariable String codigoReserva,
            @Valid @RequestBody ConfirmarRetiradaRequest request) {

        var comando = new ConfirmarRetiradaCmd(
                codigoReserva,
                request.placaVeiculo(),
                request.documentosValidos() != null && request.documentosValidos(),
                request.quilometragem(),
                request.combustivel()
        );

        var contrato = servico.confirmar(comando);
        return ResponseEntity.status(201).body(contrato);
    }
}

// DTOs de Request

record ConfirmarRetiradaRequest(
        @NotBlank(message = "Placa do veículo é obrigatória")
        String placaVeiculo,

        @NotNull(message = "Validação de documentos é obrigatória")
        Boolean documentosValidos,

        @Min(value = 0, message = "Quilometragem não pode ser negativa")
        int quilometragem,

        @NotBlank(message = "Nível de combustível é obrigatório")
        String combustivel
) {}

