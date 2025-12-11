package dev.sauloaraujo.sgb.apresentacao.locacao.operacao;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.LocacaoServicoAplicacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.Faturamento;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ProcessarDevolucaoCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/locacoes")
@Tag(name = "Devoluções", description = "Operações de devolução de veículos")
public class DevolucaoController {

    private final LocacaoServicoAplicacao servico;

    public DevolucaoController(LocacaoServicoAplicacao servico) {
        this.servico = servico;
    }

    @PostMapping("/{codigo}/devolucao")
    @Operation(summary = "Registrar Devolução", description = "Finaliza a locação e calcula o faturamento")
    public ResponseEntity<FaturamentoResponse> registrarDevolucao(
            @PathVariable String codigo,
            @Valid @RequestBody DevolucaoRequest request) {

        // 1. Converter Request (JSON) -> Command (Domínio)
        var comando = ProcessarDevolucaoCommand.builder()
                .codigoLocacao(codigo)
                .quilometragem(request.quilometragem())
                .combustivel(request.combustivel())
                .possuiAvarias(request.possuiAvarias())
                .taxaCombustivel(request.taxaExtra())
                .diasUtilizados(0)
                .diasAtraso(0)
                .percentualMultaAtraso(BigDecimal.ZERO)
                .build();

        // 2. Chamar Aplicação
        var faturamento = servico.processarDevolucao(comando);

        // 3. Retornar Resposta
        return ResponseEntity.ok(new FaturamentoResponse(
                faturamento.total(),
                faturamento.diarias(),
                faturamento.multaAtraso(),
                faturamento.taxasAdicionais()
        ));
    }
}

// DTOs Auxiliares (podem ficar em arquivos separados ou internos)

record DevolucaoRequest(
    @Min(value = 0, message = "Quilometragem inválida") 
    int quilometragem,
    
    @NotBlank(message = "Combustível obrigatório") 
    String combustivel,
    
    @NotNull 
    boolean possuiAvarias,
    
    BigDecimal taxaExtra
) {}

record FaturamentoResponse(
    BigDecimal valorTotal,
    BigDecimal valorDiarias,
    BigDecimal valorMulta,
    BigDecimal valorTaxas
) {}
