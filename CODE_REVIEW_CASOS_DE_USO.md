# 🔍 CODE REVIEW - Casos de Uso da 2ª Entrega

## 📋 ANÁLISE DAS FUNCIONALIDADES REQUERIDAS

### Status Atual das Implementações

| Funcionalidade | Status | Camada Domínio | Camada Aplicação | Camada REST |
|----------------|--------|----------------|------------------|-------------|
| 1. Criar reserva | ⚠️ PARCIAL | ✅ `Reserva` | ⏳ Falta serviço | ⏳ Falta endpoint |
| 2. Processar devolução | ⚠️ PARCIAL | ✅ `Locacao` | ⏳ Falta serviço | ⏳ Falta endpoint |
| 3. Confirmar retirada | ❌ FALTA | ⏳ Método falta | ⏳ Falta serviço | ⏳ Falta endpoint |
| 4. Agendar manutenção | ✅ IMPLEMENTADO | ✅ `Veiculo.agendarManutencao()` | ⏳ Falta serviço | ⏳ Falta endpoint |
| 5. Cancelar reserva | ✅ IMPLEMENTADO | ✅ `Reserva.cancelar()` | ⏳ Falta serviço | ⏳ Falta endpoint |
| 6. Alterar reserva | ✅ IMPLEMENTADO | ✅ `Reserva.replanejar()` | ⏳ Falta serviço | ⏳ Falta endpoint |

---

## 🔍 CODE REVIEW - Entidade Reserva

### ✅ Pontos Fortes

1. **Validação Defensiva** ✅
   ```java
   this.codigo = Objects.requireNonNull(codigo, "O código da reserva é obrigatório");
   this.categoria = Objects.requireNonNull(categoria, "A categoria é obrigatória");
   ```
   - ✅ Usa `Objects.requireNonNull`
   - ✅ Mensagens descritivas

2. **Imutabilidade Parcial** ✅
   ```java
   private final String codigo;
   private final CategoriaCodigo categoria;
   private final Cliente cliente;
   ```
   - ✅ Atributos de identidade são `final`
   - ✅ Estado (status, período) pode mudar via métodos de negócio

3. **Métodos de Negócio** ✅
   ```java
   public void concluir()
   public void cancelar()
   public void replanejar(PeriodoLocacao novoPeriodo, BigDecimal diaria)
   ```
   - ✅ Entidade Rica (comportamento + estado)
   - ✅ Encapsulamento das transições de estado

### ⚠️ Pontos de Atenção

1. **Validação de Transições de Estado** ❌ CRÍTICO
   ```java
   public void cancelar() {
       status = StatusReserva.CANCELADA; // ❌ Não valida estado atual
   }
   ```
   
   **Problema**: Permite cancelar reserva já concluída ou cancelada
   
   **Correção Necessária**:
   ```java
   public void cancelar() {
       if (status != StatusReserva.ATIVA) {
           throw new IllegalStateException(
               "Só é possível cancelar reservas ATIVAS. Status atual: " + status
           );
       }
       status = StatusReserva.CANCELADA;
   }
   ```

2. **Método `replanejar` sem Validação de Status** ❌ CRÍTICO
   ```java
   public void replanejar(PeriodoLocacao novoPeriodo, BigDecimal diaria) {
       this.periodo = Objects.requireNonNull(novoPeriodo, "O período é obrigatório");
       ajustarValorEstimado(diaria);
   }
   ```
   
   **Problema**: Permite replanejar reserva cancelada ou concluída
   
   **Correção Necessária**:
   ```java
   public void replanejar(PeriodoLocacao novoPeriodo, BigDecimal diaria) {
       if (status != StatusReserva.ATIVA) {
           throw new IllegalStateException(
               "Só é possível replanejar reservas ATIVAS. Status atual: " + status
           );
       }
       this.periodo = Objects.requireNonNull(novoPeriodo, "O período é obrigatório");
       ajustarValorEstimado(diaria);
   }
   ```

3. **Falta Método `confirmarRetirada()`** ❌ FUNCIONALIDADE FALTANTE
   
   **Necessário para Caso de Uso 3**:
   ```java
   public void confirmarRetirada() {
       if (status != StatusReserva.ATIVA) {
           throw new IllegalStateException(
               "Só é possível confirmar retirada de reservas ATIVAS. Status atual: " + status
           );
       }
       status = StatusReserva.CONCLUIDA;
   }
   ```

4. **Validação de Período** ⚠️ MELHORIA
   - Deveria validar se o novo período é futuro ao replanejar
   - Deveria impedir replanejar para data passada

---

## 🔍 CODE REVIEW - Necessidades de Implementação

### 1. ✅ Criar Reserva (Serviço de Aplicação)

**Arquivo**: `ReservaServicoAplicacao.java`

```java
@Service
@Transactional
public class ReservaServicoAplicacao {
    
    private final ReservaRepositorioAplicacao reservaRepositorio;
    private final VeiculoRepositorioAplicacao veiculoRepositorio;
    private final ApplicationEventPublisher eventPublisher;
    
    public ReservaServicoAplicacao(
        ReservaRepositorioAplicacao reservaRepositorio,
        VeiculoRepositorioAplicacao veiculoRepositorio,
        ApplicationEventPublisher eventPublisher
    ) {
        this.reservaRepositorio = Objects.requireNonNull(reservaRepositorio);
        this.veiculoRepositorio = Objects.requireNonNull(veiculoRepositorio);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }
    
    public ReservaResumo criarReserva(
        CategoriaCodigo categoria,
        String cidade,
        PeriodoLocacao periodo,
        String cpfCliente
    ) {
        // 1. Validar disponibilidade
        List<Veiculo> disponiveis = veiculoRepositorio
            .buscarDisponiveis(cidade, categoria);
        
        if (disponiveis.isEmpty()) {
            throw new IllegalStateException(
                "Não há veículos disponíveis para a categoria " + categoria
            );
        }
        
        // 2. Calcular valor estimado
        BigDecimal diaria = disponiveis.get(0).getDiaria();
        BigDecimal valorEstimado = diaria.multiply(
            BigDecimal.valueOf(periodo.dias())
        );
        
        // 3. Criar reserva (Domínio)
        Reserva reserva = new Reserva(
            categoria,
            cidade,
            periodo,
            valorEstimado,
            clienteRepositorio.buscarPorCpf(cpfCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"))
        );
        
        // 4. Persistir
        reservaRepositorio.salvar(reserva);
        
        // 5. Publicar evento (Observer Pattern)
        eventPublisher.publishEvent(new ReservaCriadaEvent(
            reserva.getCodigo(),
            reserva.getCliente().getEmail(),
            LocalDateTime.now()
        ));
        
        return reservaRepositorio.buscarPorCodigo(reserva.getCodigo())
            .orElseThrow();
    }
}
```

**Checklist**:
- ✅ @Transactional
- ✅ Validação de disponibilidade ANTES de criar
- ✅ Cria entidade via construtor do domínio
- ✅ Persiste via repositório
- ✅ Publica evento (Observer Pattern)

---

### 2. ✅ Processar Devolução e Faturamento

**Arquivo**: `LocacaoServicoAplicacao.processarDevolucao()`

```java
@Transactional
public LocacaoResumo processarDevolucao(
    String codigoLocacao,
    LocalDateTime dataDevolucao,
    ChecklistVistoria vistoria
) {
    // 1. Recuperar locação
    Locacao locacao = locacaoRepositorio.buscarPorCodigo(codigoLocacao)
        .map(this::converterParaDominio)
        .orElseThrow(() -> new IllegalArgumentException("Locação não encontrada"));
    
    // 2. Processar devolução (Domínio + Strategy Pattern)
    locacao.processar Devolucao(dataDevolucao, vistoria, estrategiaMulta);
    
    // 3. Liberar veículo
    Veiculo veiculo = veiculoRepositorio.buscarPorPlaca(locacao.getPlaca())
        .orElseThrow();
    veiculo.liberar(); // Muda status para DISPONIVEL
    
    // 4. Persistir mudanças
    locacaoRepositorio.salvar(locacao);
    veiculoRepositorio.salvar(veiculo);
    
    // 5. Publicar evento
    eventPublisher.publishEvent(new DevolucaoProcessadaEvent(
        codigoLocacao,
        locacao.getValorFinal(),
        dataDevolucao
    ));
    
    return converterParaResumo(locacao);
}
```

**Checklist**:
- ✅ @Transactional
- ✅ Recupera entidade do repositório
- ✅ Executa ação no domínio
- ✅ Atualiza veículo (transição de estado)
- ✅ Persiste ambas entidades
- ✅ Usa Strategy Pattern (cálculo de multa)
- ✅ Publica evento (Observer Pattern)

---

### 3. ✅ Confirmar Retirada e Gerar Contrato

**Método Novo no Domínio**: `Reserva.confirmarRetirada()`  
**Serviço**: `ReservaServicoAplicacao.confirmarRetirada()`

```java
@Transactional
public LocacaoResumo confirmarRetirada(
    String codigoReserva,
    String placa,
    ChecklistVistoria vistoria
) {
    // 1. Recuperar reserva
    Reserva reserva = reservaRepositorio.buscarPorCodigo(codigoReserva)
        .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
    
    // 2. Validar veículo
    Veiculo veiculo = veiculoRepositorio.buscarPorPlaca(placa)
        .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
    
    if (!veiculo.getCategoria().equals(reserva.getCategoria())) {
        throw new IllegalStateException("Veículo não pertence à categoria reservada");
    }
    
    // 3. Criar locação (Domínio)
    Locacao locacao = new Locacao(
        placa,
        reserva.getPeriodo(),
        veiculo.getDiaria(),
        reserva.getCliente().getCpfOuCnpj(),
        vistoria,
        estrategiaMulta // Strategy Pattern
    );
    
    // 4. Atualizar estados
    reserva.confirmarRetirada(); // ATIVA -> CONCLUIDA
    veiculo.alugar(); // DISPONIVEL -> ALUGADO
    
    // 5. Persistir
    locacaoRepositorio.salvar(locacao);
    reservaRepositorio.salvar(reserva);
    veiculoRepositorio.salvar(veiculo);
    
    // 6. Publicar evento
    eventPublisher.publishEvent(new RetiradaConfirmadaEvent(
        locacao.getCodigo(),
        reserva.getCodigo(),
        placa,
        LocalDateTime.now()
    ));
    
    return converterParaResumo(locacao);
}
```

**Checklist**:
- ✅ @Transactional
- ✅ Recupera Reserva e Veículo
- ✅ Valida categoria do veículo
- ✅ Cria Locação (nova entidade)
- ✅ Atualiza estados (Reserva.CONCLUIDA, Veiculo.ALUGADO)
- ✅ Persiste 3 entidades
- ✅ Publica evento

---

### 4. ✅ Agendar Manutenção de Veículo

**Já Implementado no Domínio**: `Veiculo.agendarManutencao()`  
**Serviço**: `VeiculoServicoAplicacao.agendarManutencao()`

```java
@Transactional
public VeiculoResumo agendarManutencao(
    String placa,
    LocalDate dataManutencao,
    String observacoes
) {
    // 1. Recuperar veículo
    Veiculo veiculo = veiculoRepositorio.buscarPorPlaca(placa)
        .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
    
    // 2. Agendar manutenção (Domínio valida estado)
    veiculo.agendarManutencao(dataManutencao, observacoes);
    
    // 3. Persistir
    veiculoRepositorio.salvar(veiculo);
    
    // 4. Publicar evento
    eventPublisher.publishEvent(new ManutencaoAgendadaEvent(
        placa,
        dataManutencao,
        observacoes
    ));
    
    return converterParaResumo(veiculo);
}
```

**Checklist**:
- ✅ @Transactional
- ✅ Recupera veículo
- ✅ Executa método de domínio
- ✅ Persiste
- ✅ Publica evento

---

### 5. ✅ Cancelar Reserva

**Já Implementado no Domínio**: `Reserva.cancelar()` (PRECISA CORREÇÃO)  
**Serviço**: `ReservaServicoAplicacao.cancelarReserva()`

```java
@Transactional
public void cancelarReserva(String codigoReserva) {
    // 1. Recuperar reserva
    Reserva reserva = reservaRepositorio.buscarPorCodigo(codigoReserva)
        .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
    
    // 2. Cancelar (Domínio valida estado)
    reserva.cancelar(); // Lança exceção se não ATIVA
    
    // 3. Persistir
    reservaRepositorio.salvar(reserva);
    
    // 4. Publicar evento
    eventPublisher.publishEvent(new ReservaCanceladaEvent(
        codigoReserva,
        LocalDateTime.now()
    ));
}
```

**Checklist**:
- ✅ @Transactional
- ✅ Recupera reserva
- ✅ Executa cancelamento
- ✅ Persiste
- ✅ Publica evento
- ⚠️ **REQUER**: Validação de estado no método `Reserva.cancelar()`

---

### 6. ✅ Alterar Reserva

**Já Implementado no Domínio**: `Reserva.replanejar()` (PRECISA CORREÇÃO)  
**Serviço**: `ReservaServicoAplicacao.alterarReserva()`

```java
@Transactional
public ReservaResumo alterarReserva(
    String codigoReserva,
    PeriodoLocacao novoPeriodo
) {
    // 1. Recuperar reserva
    Reserva reserva = reservaRepositorio.buscarPorCodigo(codigoReserva)
        .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
    
    // 2. Buscar diária atualizada
    List<Veiculo> disponiveis = veiculoRepositorio
        .buscarDisponiveis(reserva.getCidadeRetirada(), reserva.getCategoria());
    
    if (disponiveis.isEmpty()) {
        throw new IllegalStateException("Não há veículos disponíveis");
    }
    
    BigDecimal diaria = disponiveis.get(0).getDiaria();
    
    // 3. Replanejar (Domínio valida estado)
    reserva.replanejar(novoPeriodo, diaria);
    
    // 4. Persistir
    reservaRepositorio.salvar(reserva);
    
    // 5. Publicar evento
    eventPublisher.publishEvent(new ReservaAlteradaEvent(
        codigoReserva,
        novoPeriodo,
        reserva.getValorEstimado()
    ));
    
    return converterParaResumo(reserva);
}
```

**Checklist**:
- ✅ @Transactional
- ✅ Recupera reserva
- ✅ Busca diária atualizada
- ✅ Executa replanejamento
- ✅ Persiste
- ✅ Publica evento
- ⚠️ **REQUER**: Validação de estado no método `Reserva.replanejar()`

---

## 🎯 RESUMO DAS CORREÇÕES NECESSÁRIAS

### Camada de Domínio

1. ❌ **CRÍTICO**: Adicionar validação de estado em `Reserva.cancelar()`
2. ❌ **CRÍTICO**: Adicionar validação de estado em `Reserva.replanejar()`
3. ❌ **FUNCIONALIDADE**: Criar método `Reserva.confirmarRetirada()`
4. ⚠️ **MELHORIA**: Validar períodos futuros em `replanejar()`

### Camada de Aplicação (TODAS FALTANDO)

1. ⏳ `ReservaServicoAplicacao.criarReserva()`
2. ⏳ `LocacaoServicoAplicacao.processarDevolucao()` (já existe parcialmente)
3. ⏳ `ReservaServicoAplicacao.confirmarRetirada()`
4. ⏳ `VeiculoServicoAplicacao.agendarManutencao()`
5. ⏳ `ReservaServicoAplicacao.cancelarReserva()`
6. ⏳ `ReservaServicoAplicacao.alterarReserva()`

### Camada de Apresentação (TODAS FALTANDO)

1. ⏳ `POST /api/reservas` - Criar reserva
2. ⏳ `POST /api/locacoes/{codigo}/devolucao` - Processar devolução
3. ⏳ `POST /api/reservas/{codigo}/confirmar-retirada` - Confirmar retirada
4. ⏳ `POST /api/veiculos/{placa}/manutencao` - Agendar manutenção
5. ⏳ `DELETE /api/reservas/{codigo}` - Cancelar reserva
6. ⏳ `PUT /api/reservas/{codigo}` - Alterar reserva

---

## 📊 PRIORIZAÇÃO DE IMPLEMENTAÇÃO

### Fase 1: Correções Críticas (Domínio)
1. ✅ Corrigir `Reserva.cancelar()` - validação de estado
2. ✅ Corrigir `Reserva.replanejar()` - validação de estado
3. ✅ Criar `Reserva.confirmarRetirada()`

### Fase 2: Serviços de Aplicação
4. ✅ `ReservaServicoAplicacao` completo
5. ✅ `VeiculoServicoAplicacao.agendarManutencao()`
6. ✅ Completar `LocacaoServicoAplicacao.processarDevolucao()`

### Fase 3: Endpoints REST
7. ✅ Controllers REST para todas as 6 funcionalidades

---

**Data**: 09/12/2025  
**Status Geral**: ⚠️ **REVISÃO NECESSÁRIA**  
**Próxima Ação**: Corrigir validações de estado na entidade `Reserva`
