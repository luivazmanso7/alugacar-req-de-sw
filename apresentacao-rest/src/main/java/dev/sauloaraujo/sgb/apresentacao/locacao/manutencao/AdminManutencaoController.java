package dev.sauloaraujo.sgb.apresentacao.locacao.manutencao;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.manutencao.AgendarManutencaoCmd;
import dev.sauloaraujo.sgb.aplicacao.locacao.manutencao.ManutencaoServicoAplicacao;
import dev.sauloaraujo.sgb.aplicacao.locacao.manutencao.VeiculoManutencaoResumo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/veiculos")
@Tag(name = "Manutenção Admin", description = "Operações de manutenção de veículos para administradores")
public class AdminManutencaoController {

    private final ManutencaoServicoAplicacao manutencaoServicoAplicacao;

    public AdminManutencaoController(ManutencaoServicoAplicacao manutencaoServicoAplicacao) {
        this.manutencaoServicoAplicacao = manutencaoServicoAplicacao;
    }

    @GetMapping("/precisam-manutencao")
    @Operation(summary = "Listar veículos que precisam de manutenção",
               description = "Retorna veículos que foram enviados para manutenção devido a avarias, mas ainda não tiveram a manutenção agendada")
    public ResponseEntity<List<VeiculoManutencaoResumo>> listarQuePrecisamManutencao() {
        var veiculos = manutencaoServicoAplicacao.listarQuePrecisamManutencao();
        return ResponseEntity.ok(veiculos);
    }

    @PostMapping("/{placa}/agendar-manutencao")
    @Operation(summary = "Agendar manutenção de veículo",
               description = "Agenda manutenção para o veículo informado e publica evento de domínio")
    public ResponseEntity<Void> agendarManutencao(
            @PathVariable String placa,
            @Valid @RequestBody ManutencaoRequest request) {

        var comando = new AgendarManutencaoCmd(
                placa,
                request.previsaoTermino(),
                request.motivo());

        manutencaoServicoAplicacao.agendar(comando);

        return ResponseEntity.accepted().build();
    }
}

