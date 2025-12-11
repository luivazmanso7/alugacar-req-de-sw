package dev.sauloaraujo.sgb.apresentacao.locacao.reserva;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.CancelarReservaCmd;
import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.CancelarReservaResponse;
import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ReservaServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/reservas")
@Tag(name = "Reservas", description = "Operações de cancelamento de reservas")
public class CancelarReservaController {

    private final ReservaServicoAplicacao servico;

    public CancelarReservaController(ReservaServicoAplicacao servico) {
        this.servico = servico;
    }

    @DeleteMapping("/{codigoReserva}")
    @Operation(summary = "Cancelar reserva",
               description = "Cancela uma reserva ativa. Requer pelo menos 12 horas antes da data de retirada.")
    public ResponseEntity<CancelarReservaResponse> cancelar(@PathVariable String codigoReserva) {

        // Usa a data/hora atual como data de solicitação
        var comando = new CancelarReservaCmd(codigoReserva, LocalDateTime.now());

        // Chama o serviço de aplicação
        var response = servico.cancelar(comando);

        // Retorna resposta
        return ResponseEntity.ok(response);
    }
}

