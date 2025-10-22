# language: pt

Funcionalidade: Consultar reserva
  Como cliente
  Quero consultar informações sobre reservas
  Para entender o processo de reserva

  Cenário: Entender sistema de reservas
    Quando o cliente acessa informações sobre reservas
    Então deve ver que reservas têm códigos únicos
    E deve ver que reservas são para categorias de veículos
    E deve ver que reservas têm períodos definidos
    E deve ver que reservas têm valores estimados

  Cenário: Conhecer status de reservas
    Quando o cliente consulta possíveis status de reservas
    Então deve ver status "ATIVA" como válido
    E deve ver status "CANCELADA" como válido
    E deve ver status "CONCLUIDA" como válido
    E deve ver status "EXPIRADA" como válido
