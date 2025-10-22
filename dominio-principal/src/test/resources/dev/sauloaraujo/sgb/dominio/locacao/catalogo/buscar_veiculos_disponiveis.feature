# language: pt

Funcionalidade: Buscar veículos disponíveis
  Como Cliente
  Quero buscar veículos por cidade e período
  Para ver as opções disponíveis para locação

  Contexto:
    Dado que existem veículos cadastrados no sistema
    E os veículos estão disponíveis

  Cenário: Buscar veículos disponíveis sem filtro de categoria
    Dado que o cliente quer buscar veículos
    E informa data de retirada "2025-12-01T10:00:00"
    E informa data de devolução "2025-12-05T18:00:00"
    Quando o cliente busca veículos disponíveis
    Então deve ver a lista de veículos disponíveis
    E cada veículo deve ter placa, modelo, categoria e valor da diária

  Cenário: Buscar veículos por categoria específica
    Dado que o cliente quer buscar veículos
    E informa data de retirada "2025-12-01T10:00:00"
    E informa data de devolução "2025-12-05T18:00:00"
    E seleciona categoria "ECONOMICO"
    Quando o cliente busca veículos disponíveis
    Então deve ver apenas veículos da categoria "ECONOMICO"
    E cada veículo deve ter placa, modelo, categoria e valor da diária
