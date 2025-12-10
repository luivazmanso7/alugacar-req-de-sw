package dev.sauloaraujo.sgb.apresentacao.locacao.manutencao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.manutencao.AgendarManutencaoCmd;
import dev.sauloaraujo.sgb.aplicacao.locacao.manutencao.ManutencaoServicoAplicacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/veiculos")
@Tag(name = "Manutenção de Veículos", description = "Operações de agendamento de manutenção de veículos")
public class ManutencaoController {

    private final ManutencaoServicoAplicacao manutencaoServicoAplicacao;

    public ManutencaoController(ManutencaoServicoAplicacao manutencaoServicoAplicacao) {
        this.manutencaoServicoAplicacao = manutencaoServicoAplicacao;
    }

    @PostMapping("/{placa}/manutencao")
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
