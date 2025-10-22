# Especificação e Execução de Testes

## Especificação BDD
Os cenários Gherkin a seguir descrevem o comportamento esperado do domínio, funcionando como especificação executável:

| Funcionalidade (.feature) | Objetivo | Cenários principais |
|---------------------------|----------|---------------------|
| `reserva/criar_reserva.feature` | Orientar o processo de criação de reservas, listando dados obrigatórios e categorias disponíveis. | Entender o processo de reserva; Listar categorias; Registrar dados do cliente ao criar reserva. |
| `reserva/alterar_reserva.feature` | Replanejar reservas existentes considerando disponibilidade e recalculando valores. | Recalcular preço ao estender; Impedir alteração sem disponibilidade. |
| `reserva/cancelar_reserva.feature` | Aplicar política de cancelamento conforme antecedência. | Cancelar com antecedência sem tarifa; Bloquear cancelamento nas últimas 12 horas. |
| `operacao/confirmar_retirada.feature` | Confirmar retirada e gerar locação, validando restrições de manutenção. | Associar veículo disponível; Impedir retirada com veículo exigindo manutenção. |
| `operacao/processar_devolucao.feature` | Processar devolução e faturamento final da locação. | Encerrar locação no prazo; Cobrar taxas por atraso e avarias. |
| `manutencao/agendar_manutencao.feature` | Gerenciar agenda de manutenção preventiva da frota. | Agendar manutenção quando limite atingido; Bloquear manutenção com veículo locado. |

Cada cenário usa termos da linguagem onipresente (documentada em `dominioreadme.md`) e valida regras críticas dos subdomínios Catalogo, Reserva, Operacao e Manutencao.

## Execução Automatizada
- **Data/Hora**: 2025-10-22 20:31 -03  
- **Comando**: `mvn -q test`  
- **Resultado**: Sucesso (todos os cenários executados pelo runner `RunCucumberTest`).  

> Para repetir a execução, utilize o comando acima na raiz do repositório após garantir que Java 17+ e Maven 3.8+ estão instalados.
