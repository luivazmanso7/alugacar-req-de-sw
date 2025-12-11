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
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller REST para cancelamento de reservas.
 * Apenas coordena a requisição HTTP e delega para a camada de aplicação.
 * A validação de propriedade (cliente só pode cancelar suas próprias reservas)
 * é feita no DOMÍNIO (regra de negócio), respeitando DDD rigorosamente.
 */
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
               description = "Cancela uma reserva ativa do cliente autenticado. Requer pelo menos 12 horas antes da data de retirada.")
    public ResponseEntity<CancelarReservaResponse> cancelar(
            @PathVariable String codigoReserva,
            HttpServletRequest httpRequest) {

        // 1. Obter cliente autenticado do interceptor (já validado pelo interceptor)
        Cliente cliente = (Cliente) httpRequest.getAttribute("clienteAutenticado");
        if (cliente == null) {
            return ResponseEntity.status(401).build();
        }

        // 2. Criar comando com dados do cancelamento
        // A validação de propriedade será feita no domínio (regra de negócio)
        var comando = new CancelarReservaCmd(
                codigoReserva,
                LocalDateTime.now(),
                cliente.getCpfOuCnpj()
        );

        // 3. Chama o serviço de aplicação (que delega TODAS as regras para o domínio)
        var response = servico.cancelar(comando);

        // 4. Retorna resposta
        return ResponseEntity.ok(response);
    }
}

