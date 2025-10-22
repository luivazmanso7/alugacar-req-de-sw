# Projeto AlugaCar

Sistema de Gestao de Locacao de Veiculos modelado com DDD e arquitetura limpa. Este repositorio entrega a primeira iteracao (Entrega 1) solicitada: modelos de dominio, artefatos de contexto, cenarios BDD automatizados e codigo minimo que comprova os fluxos essenciais de reserva, retirada, devolucao, manutencao e cancelamento.

## Visao Geral
- **Objetivo**: gerenciar o ciclo completo de locacao de veiculos, do catalogo ate o faturamento.

As classes de step definitions em `dominio-principal/src/test/java/...` automatizam os fluxos com Cucumber e reutilizam o mesmo contexto de teste (`AlugacarFuncionalidade`) seguindo a abordagem do professor.

## Como Executar os Testes
### Requisitos
- Java 17+
- Maven 3.8+

### Comando
```bash
mvn -q test
```

O comando dispara a suite `RunCucumberTest`, executando todos os cenarios Gherkin. Registrar a ultima execucao (data, comando, resultado) em `execucaodeteste.md` para manter a trilha de auditoria solicitada na entrega.

## entrega 1 

- Slides da apresentacao: [Link do Canva](https://www.canva.com/design/DAG2RXDH6Nw/DfejYlCyckreDRMlaZIBUA/edit?utm_content=DAG2RXDH6Nw&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton).
- Prototipo de baixa fidelidade: [Figma](https://trio-last-16563595.figma.site/).


