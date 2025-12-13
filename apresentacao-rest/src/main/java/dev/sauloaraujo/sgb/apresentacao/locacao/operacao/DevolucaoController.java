package dev.sauloaraujo.sgb.apresentacao.locacao.operacao;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.sauloaraujo.sgb.aplicacao.locacao.operacao.LocacaoServicoAplicacao;
import dev.sauloaraujo.sgb.dominio.locacao.operacao.ProcessarDevolucaoCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/locacoes")
@Tag(name = "Devoluções Admin", description = "Operações de devolução de veículos para administradores")
public class DevolucaoController {

    private final LocacaoServicoAplicacao servico;

    public DevolucaoController(LocacaoServicoAplicacao servico) {
        this.servico = servico;
    }

    @PostMapping("/{codigo}/processar-devolucao")
    @Operation(summary = "Processar Devolução", description = "Processa a devolução de uma locação ativa e calcula o faturamento. Calcula automaticamente multa por atraso comparando data real com data prevista.")
    public ResponseEntity<FaturamentoResponse> processarDevolucao(
            @PathVariable String codigo,
            @Valid @RequestBody DevolucaoRequest request) {

        LocalDateTime dataDevolucao;
        if (request.dataDevolucao() != null && !request.dataDevolucao().isEmpty()) {
            dataDevolucao = parseDataDevolucao(request.dataDevolucao());
        } else {
            dataDevolucao = LocalDateTime.now();
        }
        
        var comando = ProcessarDevolucaoCommand.builder()
                .codigoLocacao(codigo)
                .quilometragem(request.quilometragem())
                .combustivel(request.combustivel())
                .possuiAvarias(request.possuiAvarias())
                .dataDevolucao(dataDevolucao)
                .percentualMultaAtraso(new BigDecimal("0.10"))
                .build();

        var faturamento = servico.processarDevolucao(comando);

        return ResponseEntity.ok(new FaturamentoResponse(
                faturamento.total(),
                faturamento.diarias(),
                faturamento.valorAtraso(),
                faturamento.multaAtraso(),
                faturamento.taxasAdicionais()
        ));
    }
    
    private LocalDateTime parseDataDevolucao(String dataString) {
        try {
            if (dataString.endsWith("Z") || dataString.contains("+") || 
                (dataString.contains("-") && dataString.length() > 19 && 
                 dataString.lastIndexOf("-") > 10 && dataString.lastIndexOf("-") < dataString.length() - 1)) {
                Instant instant = Instant.parse(dataString);
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            } else {
                String dataLimpa = dataString;
                if (dataLimpa.contains(".") && !dataLimpa.contains("+") && !dataLimpa.contains("Z")) {
                    int pontoIndex = dataLimpa.indexOf(".");
                    dataLimpa = dataLimpa.substring(0, pontoIndex);
                }
                return LocalDateTime.parse(dataLimpa, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        } catch (DateTimeParseException e) {
            try {
                Instant instant = Instant.parse(dataString);
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                    "Data de devolução inválida: " + dataString + 
                    ". Use formato ISO 8601 (ex: 2026-05-09T06:49:00 ou 2026-05-09T06:49:00.000Z)", ex);
            }
        }
    }
}

record DevolucaoRequest(
    @Min(value = 0, message = "Quilometragem inválida") 
    int quilometragem,
    
    @NotBlank(message = "Combustível obrigatório") 
    String combustivel,
    
    @NotNull 
    boolean possuiAvarias,
    String dataDevolucao
) {}

record FaturamentoResponse(
    BigDecimal valorTotal,
    BigDecimal valorDiarias,
    BigDecimal valorAtraso,
    BigDecimal valorMulta,
    BigDecimal valorTaxas
) {}
