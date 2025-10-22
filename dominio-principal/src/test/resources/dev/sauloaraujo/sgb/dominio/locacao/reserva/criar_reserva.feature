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
