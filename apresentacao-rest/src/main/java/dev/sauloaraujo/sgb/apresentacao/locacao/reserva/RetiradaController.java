package dev.sauloaraujo.sgb.apresentacao.locacao.reserva;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ConfirmarRetiradaCmd;
import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ConfirmarRetiradaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/reservas")
@Tag(name = "Retirada de Veículos", description = "Operações de confirmação de retirada")
public class RetiradaController {

    private final ConfirmarRetiradaService servico;

    public RetiradaController(ConfirmarRetiradaService servico) {
        this.servico = servico;
    }

    @PostMapping("/{codigoReserva}/confirmar-retirada")
    @Operation(summary = "Confirmar retirada de veículo",
               description = "Confirma a retirada de um veículo, transformando a reserva em EM_ANDAMENTO")
    public ResponseEntity<Void> confirmarRetirada(
            @PathVariable String codigoReserva,
            @Valid @RequestBody ConfirmarRetiradaRequest request) {

        var comando = new ConfirmarRetiradaCmd(
                codigoReserva,
                request.placaVeiculo(),
                request.cnhCondutor(),
                request.dataHoraRetirada(),
                request.quilometragemSaida(),
                request.nivelTanqueSaida(),
                request.observacoes()
        );

        servico.confirmarRetirada(comando);
        return ResponseEntity.status(200).build();
    }
}

record ConfirmarRetiradaRequest(
        @NotBlank(message = "Placa do veículo é obrigatória")
        String placaVeiculo,

        @NotBlank(message = "CNH do condutor é obrigatória")
        String cnhCondutor,

        @NotNull(message = "Data e hora da retirada são obrigatórias")
        LocalDateTime dataHoraRetirada,

        @Min(value = 0, message = "Quilometragem de saída não pode ser negativa")
        long quilometragemSaida,

        @NotBlank(message = "Nível do tanque é obrigatório")
        String nivelTanqueSaida,

        String observacoes
) {}

