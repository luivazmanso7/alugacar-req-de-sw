# language: pt

Funcionalidade: Criar reserva de veículo
  Como cliente
  Quero entender o processo de criação de reservas
  Para saber como garantir um veículo

  Cenário: Entender processo de reserva
    Quando o cliente consulta como fazer uma reserva
    Então deve ver que precisa informar dados pessoais
    E deve ver que precisa selecionar uma categoria
    E deve ver que precisa informar período de locação
    E deve ver que precisa informar cidade de retirada

  Cenário: Conhecer tipos de categorias disponíveis
    Dado que categorias estão disponíveis para reserva
    Quando o cliente consulta categorias para reserva
    Então deve ver categoria "ECONOMICO" disponível
    E deve ver categoria "INTERMEDIARIO" disponível
    E deve ver categoria "EXECUTIVO" disponível
    E deve ver categoria "PREMIUM" disponível
    E deve ver categoria "SUV" disponível

  Cenario: Registrar dados do cliente ao criar reserva
    Dado a categoria "ECONOMICO" com diaria base 120
    E existem 1 veiculos disponiveis da categoria "ECONOMICO"
    E existem 0 reservas ativas da categoria "ECONOMICO" de "2026-01-05T10:00" ate "2026-01-08T10:00"
    Quando eu crio uma reserva da categoria "ECONOMICO" de "2026-01-05T10:00" ate "2026-01-08T10:00"
      | nome         | cpf         | cnh         | email              | cidade    |
      | Ana Cliente | 12345678901 | 12345678901 | ana@example.com    | Recife    |
    Entao a reserva e criada com sucesso
    E o cliente da reserva possui cpf "12345678901"
    E o cliente da reserva possui email "ana@example.com"
