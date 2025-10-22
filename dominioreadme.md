dado esse dominio implemente mantendo a arquitetura que ja existe no atual projeto Adotar os níveis preliminar, estratégico, tático e operacional do
DDD
• Arquitetura limpa

Projeto AlugaCar - Documentação 
Projeto
Sistema de Gestão de Locação de Veículos
Artefatos
1. Descrição do Domínio e Linguagem Onipresente 2. Mapa de Histórias de Usuário


1. Descrição do Domínio e Linguagem Onipresente
1.1. Visão Geral (Resumo do Domínio)
O sistema "AlugaCar" tem como objetivo gerenciar o ciclo de vida completo da locação de veículos. Ele abrange desde a gestão da frota disponível para aluguel até a reserva, retirada, devolução e faturamento do serviço. O sistema deve ser capaz de lidar com diferentes categorias de veículos, políticas de precificação variáveis, gestão de clientes e manutenções da frota, garantindo uma operação eficiente e uma experiência clara para o cliente final.
O principal desafio (Core Domain) é a gestão dinâmica de reservas e precificação, que deve considerar fatores como disponibilidade de frota em tempo real, sazonalidade, duração da locação e perfil do cliente para otimizar a receita e a taxa de ocupação dos veículos.
1.2. Linguagem Onipresente (Ubiquitous Language)
Este é o nosso glossário compartilhado. Todos os termos abaixo devem ser usados de forma consistente no código, na documentação e nos testes.
Termo
Definição
Exemplo de Uso / Regras Associadas
Veículo
Representa um carro físico pertencente à locadora. Cada veículo é único, identificado pela sua placa.
Um Veículo possui atributos como placa, modelo, cor, categoria e status. Não pode ser reservado se seu Status for "Em Manutenção".
Placa
Identificador único e imutável de um Veículo.
É o ID natural do Veículo. Formato: "ABC-1234" ou "ABC1D23".
Categoria
Classificação dos veículos baseada em suas características (ex: "Econômico", "SUV", "Premium").
A Categoria define o valor da diária base.
Status do Veículo
Indica a condição atual de um Veículo.
Pode ser: "Disponível", "Reservado", "Locado", "Em Manutenção", "Vendido". As transições de status são controladas.
Cliente
Pessoa física ou jurídica que aluga um Veículo.
Um Cliente é identificado pelo seu CPF/CNPJ e possui uma CNH válida (para pessoa física).
Reserva
Um compromisso formal para alugar um Veículo de uma determinada Categoria por um período específico.
Uma Reserva possui um código único. Ela garante um veículo da categoria, não um Veículo específico (com placa).
Período de Locação
O intervalo de tempo definido pela data/hora de retirada e a data/hora de devolução previstas.
Usado para calcular o custo estimado da Reserva.
Locação (Contrato)
Representa o processo ativo de um Veículo estar em posse de um Cliente. Inicia-se na Retirada e finaliza na Devolução.
Uma Locação é criada a partir de uma Reserva no momento da Retirada. É neste momento que um Veículo específico (placa) é associado ao Cliente.
Retirada
O ato de o Cliente tomar posse do Veículo no pátio da locadora, dando início à Locação.
Na Retirada, o Status do Veículo muda de "Reservado" para "Locado". O contrato é formalizado.
Devolução
O ato de o Cliente retornar o Veículo à locadora, encerrando a Locação.
Na Devolução, é feita uma vistoria. O Status do Veículo pode mudar para "Disponível" ou "Em Manutenção".
Vistoria
Inspeção realizada no Veículo durante a Retirada e a Devolução para registrar seu estado (nível de combustível, avarias, etc.).
Uma Vistoria de devolução com avarias pode gerar uma Taxa Adicional.
Diária
O custo base para alugar um Veículo por um período de 24 horas.
O valor da Diária é definido pela Categoria do Veículo.
Taxa Adicional
Qualquer cobrança além do valor das Diárias.
Exemplos: taxa por devolução em outra cidade, taxa por avarias, multa por atraso na devolução.
Faturamento
O processo de cálculo e cobrança do valor final de uma Locação após a Devolução.
O Faturamento consolida o valor das diárias, Taxas Adicionais e descontos aplicados.
Pátio
Local físico onde os veículos da frota estão estacionados.
Um Veículo está sempre associado a um Pátio quando não está locado.
Manutenção
Processo de reparo ou revisão de um Veículo.
Um Veículo com Status "Em Manutenção" não pode ser reservado ou locado.


2. Mapa de Histórias de Usuário (User Story Map)
2.1. Atores (Personas)
Cliente: Pessoa que deseja alugar um carro.
Atendente: Funcionário da locadora que opera o balcão (reservas, retiradas, devoluções).
Gerente de Frota: Responsável por manter os veículos e o catálogo atualizados.
2.2. Jornada do Cliente
ATIVIDADE
Buscar e Reservar Veículo
Gerenciar Minha Reserva
Utilizar o Veículo
PASSOS
1. Buscar Veículos Disponíveis
2. Selecionar Opções e Fazer Reserva
3. Consultar/Alterar Reserva
Release 1 (MVP)
Como Cliente, quero buscar veículos por cidade e período para ver as opções disponíveis. Como Cliente, quero ver os detalhes de uma categoria (preço da diária, exemplo de modelo).
Como Cliente, quero escolher uma categoria de veículo (Econômico, SUV, etc.) para fazer minha reserva. Como Cliente, quero fornecer meus dados pessoais (nome, CPF, CNH) para confirmar a reserva. Como Cliente, quero receber um código de confirmação da reserva.
Como Cliente, quero poder visualizar os detalhes da minha reserva (código, datas, valor). Como Cliente, quero receber um email de confirmação com os detalhes da reserva.
Release 2 (Melhorias)
Como Cliente, quero filtrar a busca por características (ex: ar-condicionado, câmbio automático).
Como Cliente, quero poder adicionar itens opcionais (ex: cadeira de bebê, GPS) à minha reserva.
Como Cliente, quero poder cancelar minha reserva online, sujeito à política de cancelamento.
Release 3 (Futuro)
Como Cliente, quero ver avaliações de outros usuários sobre os modelos de carro.
Como Cliente, quero ter a opção de pré-pagamento com desconto.
Como Cliente, quero poder alterar a data de devolução da minha reserva.

2.3. Jornada do Atendente e Gerente de Frota
ATIVIDADE
Gerenciar Locações (Atendente)
Gerenciar Clientes (Atendente)
Gerenciar Frota (Gerente)
PASSOS
1. Processar Retirada
2. Processar Devolução
3. Cadastrar/Consultar Cliente
Release 1 (MVP)
Como Atendente, quero buscar uma reserva pelo código ou CPF do cliente. Como Atendente, quero associar um veículo específico (placa) à reserva, criando um contrato de locação. Como Atendente, quero registrar a vistoria de retirada (quilometragem, combustível). Como Atendente, quero imprimir o contrato de locação para o cliente assinar.
Como Atendente, quero registrar a devolução de um veículo, informando a placa. Como Atendente, quero realizar a vistoria de devolução (nível de combustível, avarias). Como Atendente, quero calcular e registrar taxas adicionais (multa por atraso, avarias). Como Atendente, quero finalizar a locação e emitir o recibo final para o cliente.
Como Atendente, quero poder cadastrar um novo cliente no sistema durante a primeira locação. Como Atendente, quero consultar o histórico de locações de um cliente.
Release 2 (Melhorias)


Como Atendente, quero ter um checklist digital de vistoria para anexar fotos de avarias.
Como Atendente, quero poder bloquear um cliente por inadimplência ou mau uso.
Release 3 (Futuro)
Como Atendente, quero um painel (dashboard) com as retiradas e devoluções previstas para o dia.




Cenários de teste BDD
• Automação dos cenários de teste BDD com o Cucumber para isso 



# language: pt

Funcionalidade: Buscar veículos disponíveis
  Como Cliente
  Quero buscar veículos por cidade e período
  Para ver as opções disponíveis para locação

  Contexto:
    Dado que existem veículos cadastrados no sistema
    E os veículos estão disponíveis

  Cenário: Buscar veículos disponíveis sem filtro de categoria
    Dado que o cliente quer buscar veículos
    E informa data de retirada "2025-12-01T10:00:00"
    E informa data de devolução "2025-12-05T18:00:00"
    Quando o cliente busca veículos disponíveis
    Então deve ver a lista de veículos disponíveis
    E cada veículo deve ter placa, modelo, categoria e valor da diária

  Cenário: Buscar veículos por categoria específica
    Dado que o cliente quer buscar veículos
    E informa data de retirada "2025-12-01T10:00:00"
    E informa data de devolução "2025-12-05T18:00:00"
    E seleciona categoria "ECONOMICO"
    Quando o cliente busca veículos disponíveis
    Então deve ver apenas veículos da categoria "ECONOMICO"
    E cada veículo deve ter placa, modelo, categoria e valor da diária


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
      | categoria      | nome_categoria  |
      | ECONOMICO      | Econômico       |
      | INTERMEDIARIO  | Intermediário   |
      | EXECUTIVO      | Executivo       |
      | PREMIUM        | Premium         |
      | SUV            | SUV             |

  Cenário: Consultar categoria inexistente
    Dado que existem categorias de veículos cadastradas
    Quando o cliente consulta detalhes da categoria "INEXISTENTE"
    Então deve receber erro de categoria não encontrada




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
    Quando o cliente consulta categorias para reserva
    Então deve ver categoria "ECONOMICO" disponível
    E deve ver categoria "INTERMEDIARIO" disponível
    E deve ver categoria "EXECUTIVO" disponível
    E deve ver categoria "PREMIUM" disponível
    E deve ver categoria "SUV" disponível




# language: pt

Funcionalidade: Processar devolução de veículo
  Como atendente  
  Quero processar a devolução de um veículo locado
  Para finalizar a locação e calcular valor final

  Contexto:
    Dado que existem locações ativas no sistema
    E existem veículos locados

  Cenário: Processar devolução no prazo com multa por combustível
    Dado que existe uma locação ativa com código "LOC-001"
    E a devolução está dentro do prazo
    E o veículo foi entregue com combustível "VAZIO"
    E a locação teve duração de "3" dias
    Quando o atendente processa a devolução da locação "LOC-001"
    E registra vistoria com quilometragem "50300" e combustível "VAZIO"
    E registra taxa de combustível de "120.00"
    Então o valor final deve ser "270.00"
    E deve incluir "150.00" de diárias
    E deve incluir "120.00" de taxa de combustível
    E a locação deve ter status "FINALIZADA"
    E o veículo deve voltar para status "DISPONIVEL"

  Cenário: Processar devolução em atraso com multa
    Dado que existe uma locação ativa com código "LOC-002"
    E a devolução está "2" dias em atraso
    E o veículo não possui avarias
    E a locação teve duração prevista de "5" dias
    Quando o atendente processa a devolução da locação "LOC-002"
    E registra vistoria sem avarias
    Então o valor final deve incluir multa por atraso
    E deve calcular "2" dias adicionais com taxa de multa
    E deve aplicar multa de "30%" sobre diárias em atraso
    E a locação deve ter status "FINALIZADA"
    E deve gerar faturamento detalhado





# language: pt

Funcionalidade: Processar retirada de veículo
  Como atendente
  Quero processar a retirada de um veículo reservado
  Para iniciar formalmente a locação

  Contexto:
    Dado que existem reservas ativas no sistema
    E existem veículos disponíveis para retirada

  Cenário: Processar retirada com sucesso
    Dado que existe uma reserva ativa com código "RSV-001"
    E o cliente apresenta documentos válidos
    E existe veículo da categoria disponível
    E o cliente está presente para vistoria
    Quando o atendente processa a retirada da reserva "RSV-001"
    E registra vistoria de retirada com quilometragem "50000" e combustível "CHEIO"
    E associa o veículo "ABC-1234" à locação
    Então a locação deve ser criada com sucesso
    E o veículo deve ter status "LOCADO"
    E a reserva deve ter status "CONCLUIDA"
    E deve gerar contrato de locação

  Cenário: Rejeitar retirada por documentos inválidos
    Dado que existe uma reserva ativa com código "RSV-002"
    E o cliente apresenta CNH vencida
    Quando o atendente tenta processar a retirada da reserva "RSV-002"
    Então a retirada deve ser rejeitada
    E deve exibir mensagem "CNH vencida. Renovação necessária"
    E a reserva deve permanecer "ATIVA"
    E nenhum veículo deve ser associado

