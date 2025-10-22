# language: pt

Funcionalidade: Agendar manutencao preventiva da frota

  Contexto:
    Dado que o catalogo de categorias esta limpo
    E que nao existem reservas cadastradas
    E que nao existem contratos de locacao ativos

  Cenario: Agendar manutencao quando limite foi atingido
    Dado a categoria "SUV" com diaria base 230
    E existe um veiculo elegivel para manutencao da categoria "SUV" com placa "JKL4M56"
    Quando eu agendo manutencao para o veiculo "JKL4M56" com previsao "2026-07-20T10:00" e motivo "Troca de oleo"
    Entao o veiculo "JKL4M56" deve ficar em manutencao com nota "Troca de oleo"

  Cenario: Bloquear manutencao enquanto o veiculo esta locado
    Dado a categoria "ECONOMICO" com diaria base 120
    E existe um veiculo disponivel da categoria "ECONOMICO" com placa "LMN5N67"
    E existe uma reserva confirmada "R400001" da categoria "ECONOMICO" de "2026-07-01T09:00" ate "2026-07-05T09:00" para o cliente "11122233344"
    E a reserva "R400001" foi convertida em locacao com o veiculo "LMN5N67" na data "2026-07-01T09:00" com odometro 10000 e combustivel 100
    Quando eu agendo manutencao para o veiculo "LMN5N67" com previsao "2026-07-03T10:00" e motivo "Checklist"
    Entao ocorre um erro de negocio com a mensagem "Veículo não pode entrar em manutenção enquanto reservado ou locado"
