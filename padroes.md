# Padrões de Projeto Adotados

Este documento apresenta os padrões de projeto adotados no sistema AlugaCar e lista as classes criadas ou alteradas em decorrência de cada padrão implementado.

## 1. Strategy Pattern

O padrão Strategy foi adotado para permitir diferentes políticas de cálculo de multa por atraso na devolução de veículos, sem modificar a lógica da entidade Locacao.

**Classes criadas:**
- `dominio-principal/src/main/java/dev/sauloaraujo/sgb/dominio/locacao/operacao/CalculoMultaStrategy.java`
- `dominio-principal/src/main/java/dev/sauloaraujo/sgb/dominio/locacao/operacao/MultaPadraoStrategy.java`
- `dominio-principal/src/main/java/dev/sauloaraujo/sgb/dominio/locacao/operacao/MultaIsentaStrategy.java`

**Classes alteradas:**
- `dominio-principal/src/main/java/dev/sauloaraujo/sgb/dominio/locacao/operacao/Locacao.java`

A classe Locacao recebe uma instância de CalculoMultaStrategy através do construtor e delega o cálculo de multas para essa estratégia. Isso permite aplicar diferentes políticas de multa (padrão, isenta, ou futuras implementações como multa progressiva ou fixa) sem alterar o código da entidade.

## 2. Observer Pattern

O padrão Observer foi implementado para desacoplar a publicação de eventos de domínio dos componentes que reagem a esses eventos. Quando um veículo é agendado para manutenção, um evento é publicado e listeners registrados podem reagir a esse evento.

**Classes criadas:**
- `dominio-principal/src/main/java/dev/sauloaraujo/sgb/dominio/locacao/evento/VeiculoFoiParaManutencaoEvent.java`
- `aplicacao-locacao/src/main/java/dev/sauloaraujo/sgb/aplicacao/locacao/manutencao/NotificacaoGerenteListener.java`

**Classes alteradas:**
- `aplicacao-locacao/src/main/java/dev/sauloaraujo/sgb/aplicacao/locacao/manutencao/ManutencaoServicoAplicacao.java`

O ManutencaoServicoAplicacao utiliza ApplicationEventPublisher do Spring Framework para publicar eventos. O NotificacaoGerenteListener, anotado com @EventListener, é automaticamente registrado pelo Spring e reage aos eventos VeiculoFoiParaManutencaoEvent publicados. Novos listeners podem ser adicionados sem modificar o código que dispara os eventos.

## 3. Proxy Pattern

O padrão Proxy foi implementado para adicionar funcionalidade de cache ao repositório de reservas sem modificar a implementação original do repositório.

**Classes criadas:**
- `infraestrutura-persistencia-jpa/src/main/java/dev/sauloaraujo/sgb/infraestrutura/persistencia/jpa/reserva/ReservaRepositorioProxy.java`

**Classes alteradas:**
- Nenhuma

A classe ReservaRepositorioProxy implementa a interface ReservaRepositorio e mantém uma referência para a implementação real do repositório. O proxy intercepta as chamadas ao método buscarPorCodigo, verifica primeiro o cache em memória (implementado com ConcurrentHashMap) e, caso não encontre, delega a chamada para o repositório real, armazenando o resultado no cache. O proxy é transparente para os clientes, pois implementa a mesma interface do repositório original.

## 4. Iterator Pattern

O padrão Iterator foi adotado através do uso extensivo da Streams API do Java 8+, que implementa implicitamente esse padrão. A Streams API permite processar coleções de forma funcional e desacoplada.

**Classes alteradas:**
- `dominio-principal/src/test/java/dev/sauloaraujo/sgb/dominio/locacao/infra/InMemoryRepositorio.java`
- `dominio-principal/src/main/java/dev/sauloaraujo/sgb/dominio/locacao/reserva/ReservaServico.java`
- `aplicacao-locacao/src/main/java/dev/sauloaraujo/sgb/aplicacao/locacao/manutencao/ManutencaoServicoAplicacao.java`
- `infraestrutura-persistencia-jpa/src/main/java/dev/sauloaraujo/sgb/infraestrutura/persistencia/jpa/adapter/LocacaoRepositorioJpaAdapter.java`

O InMemoryRepositorio utiliza streams para filtrar veículos disponíveis por cidade e categoria. O ReservaServico utiliza streams para filtrar reservas conflitantes. O ManutencaoServicoAplicacao utiliza streams para converter listas de veículos em DTOs. O LocacaoRepositorioJpaAdapter utiliza streams para converter listas de entidades JPA em objetos de domínio. Em todos esses casos, os métodos stream(), filter(), map(), collect() e toList() são utilizados para processar coleções de forma funcional.

## 5. Template Method Pattern

O padrão Template Method foi implementado na camada de testes para definir um esqueleto comum de algoritmo que é compartilhado por múltiplas classes de funcionalidades de teste.

**Classes criadas:**
- `dominio-principal/src/test/java/dev/sauloaraujo/sgb/dominio/locacao/AlugacarFuncionalidade.java`
- `dominio-principal/src/test/java/dev/sauloaraujo/sgb/dominio/locacao/CriarReservaFuncionalidade.java`
- `dominio-principal/src/test/java/dev/sauloaraujo/sgb/dominio/locacao/CancelarReservaFuncionalidade.java`
- Outras classes de funcionalidades que estendem AlugacarFuncionalidade

**Classes alteradas:**
- Nenhuma

A classe abstrata AlugacarFuncionalidade define métodos template compartilhados como limparContexto(), registrarCategoriaPadrao(), registrarReserva() e outros métodos auxiliares comuns aos testes. As classes filhas, como CriarReservaFuncionalidade e CancelarReservaFuncionalidade, herdam esses métodos e implementam apenas os passos específicos de cada funcionalidade, seguindo o padrão de step definitions do Cucumber.

## 6. Decorator Pattern

O padrão Decorator não foi implementado explicitamente no sistema. Os interceptors de autenticação (AdminInterceptor e AutenticacaoInterceptor) poderiam ser considerados uma forma de decorator, mas são mais próximos do padrão Chain of Responsibility ou Proxy, pois interceptam requisições HTTP antes que cheguem aos controllers, não decorando objetos existentes.

---

## Resumo

O sistema implementa 5 dos 6 padrões solicitados nos requisitos da segunda entrega:

1. Strategy Pattern - Implementado
2. Observer Pattern - Implementado
3. Proxy Pattern - Implementado
4. Iterator Pattern - Implementado
5. Template Method Pattern - Implementado
6. Decorator Pattern - Não implementado

O requisito de adotar 4 ou mais padrões entre Iterator, Decorator, Observer, Proxy, Strategy e Template Method foi atendido, pois o sistema implementa 5 padrões.
