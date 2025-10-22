# language: pt

Funcionalidade: Processar retirada de veículo
  Como atendente
  Quero processar a retirada de um veículo reservado
  Para iniciar formalmente a locação

  Contexto:
    Dado que existem reservas ativas no sistema
    E existem veículos disponíveis para retirada

  Cenário: Processar retirada com sucesso
    Dado que existe uma reserva ativa com código "RSV-001"
    E o cliente apresenta documentos válidos
    E existe veículo da categoria disponível
    E o cliente está presente para vistoria
    Quando o atendente processa a retirada da reserva "RSV-001"
    E registra vistoria de retirada com quilometragem "50000" e combustível "CHEIO"
    E associa o veículo "ABC-1234" à locação
    Então a locação deve ser criada com sucesso
    E o veículo deve ter status "LOCADO"
    E a reserva deve ter status "CONCLUIDA"
    E deve gerar contrato de locação

  Cenário: Rejeitar retirada por documentos inválidos
    Dado que existe uma reserva ativa com código "RSV-002"
    E o cliente apresenta CNH vencida
    Quando o atendente tenta processar a retirada da reserva "RSV-002"
    Então a retirada deve ser rejeitada
    E deve exibir mensagem "CNH vencida. Renovação necessária"
    E a reserva deve permanecer "ATIVA"
    E nenhum veículo deve ser associado
