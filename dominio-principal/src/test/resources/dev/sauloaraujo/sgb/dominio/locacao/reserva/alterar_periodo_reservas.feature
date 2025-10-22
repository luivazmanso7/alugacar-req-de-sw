# language: pt

Funcionalidade: Alterar periodo de reservas

  Contexto:
    Dado que o catalogo de categorias esta limpo
    E que nao existem reservas cadastradas
    E que nao existem contratos de locacao ativos

  Cenario: Recalcular preco ao estender a reserva
    Dado a categoria "SUV" com diaria base 210
    E existem 4 veiculos disponiveis da categoria "SUV"
    E existem 1 reservas ativas da categoria "SUV" de "2026-07-10T10:00" ate "2026-07-12T10:00"
    E existe uma reserva confirmada "R300001" da categoria "SUV" de "2026-07-10T10:00" ate "2026-07-12T10:00" para o cliente "32165498700"
    Quando eu altero a reserva "R300001" para o periodo de "2026-07-10T10:00" ate "2026-07-15T10:00"
    Entao o periodo da reserva deve ser atualizado para "2026-07-10T10:00" ate "2026-07-15T10:00"
    E o valor total da reserva replanejada deve ser maior que 1000

  Cenario: Impedir alteracao quando nao ha disponibilidade
    Dado a categoria "ECONOMICO" com diaria base 120
    E existem 2 veiculos disponiveis da categoria "ECONOMICO"
    E existem 2 reservas ativas da categoria "ECONOMICO" de "2026-08-01T09:00" ate "2026-08-05T09:00"
    E existe uma reserva confirmada "R300002" da categoria "ECONOMICO" de "2026-08-01T09:00" ate "2026-08-05T09:00" para o cliente "65498732100"
    Quando eu altero a reserva "R300002" para o periodo de "2026-08-02T09:00" ate "2026-08-07T09:00"
    Entao ocorre um erro de negocio com a mensagem "Período indisponível para a categoria desejada"
