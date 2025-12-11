package dev.sauloaraujo.sgb.apresentacao.locacao.reserva;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ListarReservasClienteServico;
import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ReservaResumo;
import dev.sauloaraujo.sgb.dominio.locacao.cliente.Cliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller REST para listar reservas do cliente logado.
 */
@RestController
@RequestMapping("/reservas")
@Tag(name = "Reservas", description = "Operações de consulta de reservas")
public class ListarReservasClienteController {
    
    private final ListarReservasClienteServico servico;
    
    public ListarReservasClienteController(ListarReservasClienteServico servico) {
        this.servico = servico;
    }
    
    @GetMapping("/minhas")
    @Operation(summary = "Listar minhas reservas",
               description = "Retorna todas as reservas do cliente autenticado")
    public ResponseEntity<List<ReservaResumo>> listarMinhasReservas(HttpServletRequest request) {
        // Obter cliente autenticado do interceptor
        Cliente cliente = (Cliente) request.getAttribute("clienteAutenticado");
        if (cliente == null) {
            return ResponseEntity.status(401).build();
        }
        
        // Listar reservas do cliente
        List<ReservaResumo> reservas = servico.listarPorCliente(cliente.getCpfOuCnpj());
        
        return ResponseEntity.ok(reservas);
    }
}

