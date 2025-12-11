package dev.sauloaraujo.sgb.apresentacao.locacao.reserva;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.BuscarReservaServico;
import dev.sauloaraujo.sgb.aplicacao.locacao.reserva.ReservaResumo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/reservas")
@Tag(name = "Reservas", description = "Operações de consulta de reservas")
public class BuscarReservaController {

    private final BuscarReservaServico buscarReservaServico;

    public BuscarReservaController(BuscarReservaServico buscarReservaServico) {
        this.buscarReservaServico = buscarReservaServico;
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Buscar reserva por código",
               description = "Retorna um resumo da reserva correspondente ao código informado")
    public ResponseEntity<ReservaResumo> buscarPorCodigo(@PathVariable String codigo) {
        var resumo = buscarReservaServico.buscar(codigo);
        return ResponseEntity.ok(resumo);
    }

    @GetMapping
    @Operation(summary = "Listar todas as reservas",
               description = "Retorna todas as reservas cadastradas. Endpoint para administradores.")
    public ResponseEntity<List<ReservaResumo>> listarTodas() {
        var reservas = buscarReservaServico.listarTodas();
        return ResponseEntity.ok(reservas);
    }
}

