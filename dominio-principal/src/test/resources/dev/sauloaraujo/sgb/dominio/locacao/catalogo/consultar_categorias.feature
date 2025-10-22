# language: pt

Funcionalidade: Consultar categorias de veículos
  Como Cliente
  Quero ver os detalhes de uma categoria (preço da diária, exemplo de modelo)
  Para conhecer as opções e valores disponíveis

  Cenário: Listar todas as categorias disponíveis
    Dado que existem categorias de veículos cadastradas
    Quando o cliente consulta as categorias
    Então deve ver a lista de todas as categorias
    E cada categoria deve ter nome, descrição, valor da diária e exemplos de modelos

  Cenário: Consultar detalhes de categoria específica
    Dado que existem categorias de veículos cadastradas
    Quando o cliente consulta detalhes da categoria "ECONOMICO"
    Então deve ver o nome "Econômico"
    E deve ver a descrição da categoria
    E deve ver o valor da diária
    E deve ver lista de exemplos de modelos
    E deve ver quantidade de veículos disponíveis

  Esquema do Cenário: Consultar diferentes categorias
    Dado que existem categorias de veículos cadastradas
    Quando o cliente consulta detalhes da categoria "<categoria>"
    Então deve ver o nome "<nome_categoria>"
    E deve ver informações específicas da categoria

    Exemplos:
      | categoria     | nome_categoria |
      | ECONOMICO     | Econômico      |
      | INTERMEDIARIO | Intermediário  |
      | EXECUTIVO     | Executivo      |
      | PREMIUM       | Premium        |
      | SUV           | SUV            |

  Cenário: Consultar categoria inexistente
    Dado que existem categorias de veículos cadastradas
    Quando o cliente consulta detalhes da categoria "INEXISTENTE"
    Então deve receber erro de categoria não encontrada
