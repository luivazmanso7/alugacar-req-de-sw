# language: pt

Funcionalidade: Processar devolucao de locacoes

  Contexto:
    Dado que o catalogo de categorias esta limpo
    E que nao existem reservas cadastradas
    E que nao existem contratos de locacao ativos

  Cenario: Encerrar locacao no prazo sem cobrancas adicionais
    Dado a categoria "ECONOMICO" com diaria base 120
    E existe um veiculo disponivel da categoria "ECONOMICO" com placa "HJK3L45"
    E existe uma reserva confirmada "R100001" da categoria "ECONOMICO" de "2026-04-01T10:00" ate "2026-04-04T10:00" para o cliente "12312312300"
    E a reserva "R100001" foi convertida em locacao com o veiculo "HJK3L45" na data "2026-04-01T09:50" com odometro 20000 e combustivel 100
    Quando eu processo a devolucao da reserva "R100001" na data "2026-04-04T09:30" com odometro 20300 e combustivel 100 sem danos
    Entao o valor final da locacao deve ser igual ao valor estimado da reserva
    E o veiculo "HJK3L45" retorna ao patio da cidade "São Paulo"
    E o veiculo "HJK3L45" fica disponivel para nova locacao

  Cenario: Cobrar taxas por atraso e avarias enviando veiculo para manutencao
    Dado a categoria "SUV" com diaria base 250
    E existe um veiculo disponivel da categoria "SUV" com placa "XYZ9A88"
    E existe uma reserva confirmada "R100002" da categoria "SUV" de "2026-04-10T10:00" ate "2026-04-15T10:00" para o cliente "45645645600"
    E a reserva "R100002" foi convertida em locacao com o veiculo "XYZ9A88" na data "2026-04-10T09:45" com odometro 50000 e combustivel 100
    Quando eu processo a devolucao da reserva "R100002" na data "2026-04-16T12:00" com odometro 50750 e combustivel 60 com danos e taxa adicional 800.00
    Entao o valor final da locacao deve ser maior que o valor estimado da reserva
    E o veiculo "XYZ9A88" deve ser enviado para manutencao
