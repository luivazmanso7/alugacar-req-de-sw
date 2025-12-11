# ✅ Checklist de Implementação - AlugaCar

## 📊 Status Geral: 33% Completo

---

## 1️⃣ Criar Reserva

### Camada de Domínio
- [x] `ReservaServico.criarReserva()`
- [x] Validação de disponibilidade
- [x] Cálculo de valor estimado
- [x] Testes unitários

### Camada de Aplicação
- [ ] `ReservaServicoAplicacao.criar()`
- [ ] `CriarReservaCmd` (Command)
- [ ] Transação (`@Transactional`)

### Camada de Apresentação (REST)
- [ ] `POST /api/v1/reservas`
- [ ] `CriarReservaRequest` (DTO)
- [ ] `ReservaResponse` (DTO)
- [ ] Documentação Swagger
- [ ] Validações (`@Valid`)

**Status:** 🟡 40% - Falta API REST

---

## 2️⃣ Processar Devolução e Faturamento

### Camada de Domínio
- [x] `DevolucaoServico.processar()`
- [x] `Faturamento` (Value Object)
- [x] Cálculo de multas e taxas
- [x] Testes unitários

### Camada de Aplicação
- [x] `LocacaoServicoAplicacao.processarDevolucao()`
- [x] `ProcessarDevolucaoCommand`
- [x] Transação (`@Transactional`)

### Camada de Apresentação (REST)
- [x] `POST /api/v1/locacoes/{codigo}/devolucao`
- [x] `DevolucaoRequest` (DTO)
- [x] `FaturamentoResponse` (DTO)
- [x] Documentação Swagger
- [x] Validações (`@Valid`)

**Status:** ✅ 100% - COMPLETO

**Testes:** ✅ Funcionando (202 Accepted)

---

## 3️⃣ Confirmar Retirada e Gerar Contrato

### Camada de Domínio
- [x] `RetiradaServico.confirmarRetirada()`
- [x] Criação de `Locacao` a partir de `Reserva`
- [x] `ChecklistVistoria` (Embeddable)
- [x] Testes unitários

### Camada de Aplicação
- [ ] `RetiradaServicoAplicacao.confirmar()`
- [ ] `ConfirmarRetiradaCmd` (Command)
- [ ] Transação (`@Transactional`)

### Camada de Apresentação (REST)
- [ ] `POST /api/v1/reservas/{codigo}/confirmar-retirada`
- [ ] `ConfirmarRetiradaRequest` (DTO)
- [ ] `ContratoResponse` (DTO)
- [ ] Gerador de Contrato (PDF/JSON)
- [ ] Documentação Swagger
- [ ] Validações (`@Valid`)

**Status:** 🟡 35% - Falta API REST e Geração de Contrato

---

## 4️⃣ Agendar Manutenção de Veículo

### Camada de Domínio
- [x] `ManutencaoServico` (lógica de negócio)
- [x] Validações de data
- [x] Evento de domínio
- [x] Testes unitários

### Camada de Aplicação
- [x] `ManutencaoServicoAplicacao.agendar()`
- [x] `AgendarManutencaoCmd`
- [x] Publicação de eventos

### Camada de Apresentação (REST)
- [x] `POST /api/v1/veiculos/{placa}/manutencao`
- [x] `ManutencaoRequest` (DTO)
- [x] Documentação Swagger
- [x] Validações (`@Valid`)

**Status:** ✅ 100% - COMPLETO

**Testes:** ✅ Funcionando (202 Accepted)

---

## 5️⃣ Cancelar Reserva

### Camada de Domínio
- [x] `ReservaCancelamentoServico.cancelar()`
- [x] Validação de prazo (12h antes)
- [x] Cálculo de tarifa de cancelamento
- [x] Mudança de status
- [x] Testes unitários

### Camada de Aplicação
- [ ] `ReservaServicoAplicacao.cancelar()`
- [ ] `CancelarReservaCmd` (Command)
- [ ] Transação (`@Transactional`)

### Camada de Apresentação (REST)
- [ ] `DELETE /api/v1/reservas/{codigo}` ou `POST /api/v1/reservas/{codigo}/cancelar`
- [ ] `CancelarReservaResponse` (DTO com tarifa)
- [ ] Documentação Swagger

**Status:** 🟡 40% - Falta API REST

---

## 6️⃣ Alterar Reserva

### Camada de Domínio
- [x] `ReservaReplanejamentoServico.replanejar()`
- [x] Validação de disponibilidade
- [x] Verificação de conflitos
- [x] Recálculo de valor
- [x] Testes unitários

### Camada de Aplicação
- [ ] `ReservaServicoAplicacao.alterar()`
- [ ] `AlterarReservaCmd` (Command)
- [ ] Transação (`@Transactional`)

### Camada de Apresentação (REST)
- [ ] `PUT /api/v1/reservas/{codigo}` ou `PATCH /api/v1/reservas/{codigo}/periodo`
- [ ] `AlterarReservaRequest` (DTO)
- [ ] `ReservaResponse` (DTO)
- [ ] Documentação Swagger
- [ ] Validações (`@Valid`)

**Status:** 🟡 40% - Falta API REST

---

## 📈 Progresso por Camada

### Camada de Domínio
- [x] 6/6 funcionalidades (100%)

### Camada de Aplicação
- [x] 2/6 funcionalidades (33%)
- [ ] Criar Reserva
- [ ] Confirmar Retirada
- [ ] Cancelar Reserva
- [ ] Alterar Reserva

### Camada de Apresentação REST
- [x] 2/6 funcionalidades (33%)
- [ ] Criar Reserva
- [ ] Confirmar Retirada
- [ ] Cancelar Reserva
- [ ] Alterar Reserva

---

## 🎯 Próximos Passos (Por Prioridade)

### 🔴 ALTA PRIORIDADE (Fluxo Principal)

#### 1. Criar Reserva (4h)
```
□ Criar ReservaServicoAplicacao
□ Criar CriarReservaController
□ DTOs: CriarReservaRequest, ReservaResponse
□ Testes da API
```

#### 2. Confirmar Retirada (4h)
```
□ Criar RetiradaServicoAplicacao
□ Criar RetiradaController
□ DTOs: ConfirmarRetiradaRequest, ContratoResponse
□ Implementar gerador de contrato
□ Testes da API
```

### 🟠 MÉDIA PRIORIDADE (Funcionalidades Essenciais)

#### 3. Cancelar Reserva (2h)
```
□ Adicionar método em ReservaServicoAplicacao
□ Criar endpoint em ReservaController
□ DTO: CancelarReservaResponse
□ Testes da API
```

#### 4. Alterar Reserva (3h)
```
□ Adicionar método em ReservaServicoAplicacao
□ Criar endpoint em ReservaController
□ DTOs: AlterarReservaRequest, ReservaResponse
□ Testes da API
```

### 🟢 BAIXA PRIORIDADE (Melhorias)

#### 5. Melhorar Tratamento de Erros (2h)
```
□ Criar exceções de negócio personalizadas
□ Melhorar GlobalExceptionHandler
□ Retornar 400 ao invés de 500 nas validações
□ Mensagens de erro mais claras
```

#### 6. Melhorar Testes (1h)
```
□ Atualizar test-api.sh com códigos corretos
□ Adicionar testes de integração
□ Testar cenários de erro
```

---

## ⏱️ Estimativa de Tempo

| Fase | Horas | Descrição |
|------|-------|-----------|
| Fase 1 | 8h | Criar Reserva + Confirmar Retirada |
| Fase 2 | 5h | Cancelar + Alterar Reserva |
| Fase 3 | 3h | Melhorias e Testes |
| **TOTAL** | **16h** | **Estimativa para 100%** |

---

## 📝 Padrão de Implementação

### Para cada funcionalidade, siga este template:

#### 1. Serviço de Aplicação
```java
@Service
public class ReservaServicoAplicacao {
    private final ReservaServico servicoDominio;
    
    @Transactional
    public ReservaResponse criar(CriarReservaCmd cmd) {
        // Delegue para o domínio
        var reserva = servicoDominio.criarReserva(...);
        // Converta para DTO
        return toResponse(reserva);
    }
}
```

#### 2. Controller REST
```java
@RestController
@RequestMapping("/reservas")
public class ReservaController {
    private final ReservaServicoAplicacao servico;
    
    @PostMapping
    public ResponseEntity<ReservaResponse> criar(
        @Valid @RequestBody CriarReservaRequest request) {
        var cmd = toCommand(request);
        var response = servico.criar(cmd);
        return ResponseEntity.status(201).body(response);
    }
}
```

#### 3. DTOs
```java
record CriarReservaRequest(
    @NotBlank String categoriaCodigo,
    @NotBlank String cidadeRetirada,
    @NotNull PeriodoLocacaoDto periodo,
    @Valid ClienteDto cliente
) {}

record ReservaResponse(
    String codigo,
    String status,
    BigDecimal valorEstimado,
    // ...
) {}
```

---

## 🔍 Como Usar Este Checklist

1. **Marque [x] quando completar cada item**
2. **Priorize os itens 🔴 ALTA PRIORIDADE primeiro**
3. **Teste após cada implementação**
4. **Atualize a porcentagem de status**
5. **Commit após cada funcionalidade completa**

---

**Última Atualização:** 10 de dezembro de 2025  
**Próxima Revisão:** Após implementar Criar Reserva
