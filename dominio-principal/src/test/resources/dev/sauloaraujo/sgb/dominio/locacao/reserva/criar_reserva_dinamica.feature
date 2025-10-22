# language: pt

Funcionalidade: Criacao de reservas com precificacao dinamica

  Contexto:
    Dado que o catalogo de categorias esta limpo
    E que nao existem reservas cadastradas

  Cenario: Calcular preco dinamico em alta demanda
    Dado a categoria "SUV" com diaria base 200
    E existem 4 veiculos disponiveis da categoria "SUV"
    E existem 3 reservas ativas da categoria "SUV" de "2026-01-10T10:00" ate "2026-01-15T10:00"
    Quando eu crio uma reserva da categoria "SUV" de "2026-01-10T10:00" ate "2026-01-15T10:00"
      | nome       | cpf         | cnh         | email            | cidade    |
      | Joao Dias  | 12345678901 | 12345678901 | joao@example.com | Fortaleza |
    Entao a reserva e criada com sucesso
    E o valor final deve ser maior que 1200

  Cenario: Bloquear reserva quando nao ha disponibilidade
    Dado a categoria "ECONOMICO" com diaria base 120
    E existem 1 veiculos disponiveis da categoria "ECONOMICO"
    E existem 1 reservas ativas da categoria "ECONOMICO" de "2026-02-01T09:00" ate "2026-02-05T09:00"
    Quando eu tento criar uma reserva da categoria "ECONOMICO" de "2026-02-01T09:00" ate "2026-02-05T09:00"
      | nome        | cpf         | cnh         | email             | cidade |
      | Maria Souza | 98765432100 | 98765432100 | maria@example.com | Recife |
    Entao ocorre um erro de negocio com a mensagem "Não há veículos disponíveis para o período selecionado"
