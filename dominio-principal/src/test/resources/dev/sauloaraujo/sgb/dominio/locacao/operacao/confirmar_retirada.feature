# language: pt

Funcionalidade: Confirmar retirada e gerar locacao

  Contexto:
    Dado que o catalogo de categorias esta limpo
    E que nao existem reservas cadastradas
    E que nao existem contratos de locacao ativos

  Cenario: Associar veiculo disponivel a uma reserva confirmada
    Dado a categoria "SUV" com diaria base 220
    E existe um veiculo disponivel da categoria "SUV" com placa "ABC1D23"
    E existe uma reserva confirmada "R000001" da categoria "SUV" de "2026-03-01T09:00" ate "2026-03-05T09:00" para o cliente "45678912300"
    Quando eu confirmo a retirada da reserva "R000001" com o veiculo "ABC1D23"
      | combustivel | odometro | notas              |
      | 80          | 45000    | vistoria sem danos |
    Entao o contrato de locacao e criado para o veiculo "ABC1D23"
    E a reserva "R000001" passa a ter o status "CONCLUIDA"

  Cenario: Impedir retirada com veiculo exigindo manutencao
    Dado a categoria "SUV" com diaria base 220
    E existe um veiculo com manutencao pendente da categoria "SUV" com placa "DEF2E34"
    E existe uma reserva confirmada "R000002" da categoria "SUV" de "2026-03-10T09:00" ate "2026-03-15T09:00" para o cliente "56789123400"
    Quando eu confirmo a retirada da reserva "R000002" com o veiculo "DEF2E34"
      | combustivel | odometro | notas              |
      | 90          | 52000    | veiculo com alerta |
    Entao ocorre um erro de negocio com a mensagem "Veículo selecionado precisa passar por manutenção"

  Cenario: Impedir retirada com veiculo vendido
    Dado que o catalogo de categorias esta limpo
    E que nao existem reservas cadastradas
    E que nao existem contratos de locacao ativos
    E a categoria "SUV" com diaria base 220
    E existe um veiculo vendido da categoria "SUV" com placa "VND9Z99"
    E existe uma reserva confirmada "R000003" da categoria "SUV" de "2026-03-20T09:00" ate "2026-03-25T09:00" para o cliente "98765432100"
    Quando eu confirmo a retirada da reserva "R000003" com o veiculo "VND9Z99"
      | combustivel | odometro | notas           |
      | 70          | 60000    | tentativa venda |
    Entao ocorre um erro de negocio com a mensagem "Veículo vendido não pode ser locado"
