# language: pt

Funcionalidade: Cancelar reservas conforme politica de cobranca

  Contexto:
    Dado que o catalogo de categorias esta limpo
    E que nao existem reservas cadastradas

  Cenario: Cancelar com antecedencia sem tarifa
    Dado a categoria "ECONOMICO" com diaria base 130
    E existe uma reserva confirmada "R700001" da categoria "ECONOMICO" de "2026-05-20T10:00" ate "2026-05-25T10:00" para o cliente "55566677700"
    Quando eu solicito o cancelamento da reserva "R700001" em "2026-05-16T09:00"
    Entao o cancelamento e realizado com tarifa 0.00

  Cenario: Impedir cancelamento dentro de 12 horas da retirada
    Dado a categoria "SUV" com diaria base 260
    E existe uma reserva confirmada "R700002" da categoria "SUV" de "2026-06-10T10:00" ate "2026-06-15T10:00" para o cliente "99988877700"
    Quando eu solicito o cancelamento da reserva "R700002" em "2026-06-10T00:30"
    Entao ocorre um erro de negocio com a mensagem "Cancelamento não permitido nas últimas 12 horas"
