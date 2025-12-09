Contexto do Projeto: Estou desenvolvendo o sistema SGB (Sistema de Gestão de Biblioteca/Locação) seguindo uma arquitetura estrita de Clean Architecture + DDD. Você deve atuar como um Arquiteto de Software Java Sênior e seguir rigorosamente as regras abaixo.

Regras de Ouro (Padrão do Projeto):

Camada de Domínio (dominio-principal):

Classes Puras (sem anotações de Framework como Spring ou JPA).

Validação Defensiva: Use org.apache.commons.lang3.Validate (ex: notNull, notBlank) dentro dos construtores.

Imutabilidade: Prefira atributos private final e inicialização via construtor.

Eventos: Use records para Domain Events.

Camada de Aplicação (aplicacao-locacao):

DTOs: Devem ser Interfaces Públicas (Projections), sufixo Resumo (ex: ClienteResumo). Apenas Getters.

Repositórios: Interfaces definindo contratos de persistência.

Serviços: Classes públicas, sufixo ServicoAplicacao.

Injeção de dependência via Construtor.

Atributos private final.

Validação de dependências com Validate.notNull.

Camada de Infraestrutura (infraestrutura-persistencia-jpa):

Ocultamento: Entidades JPA (@Entity) e Interfaces Spring Data (extends JpaRepository) devem ser package-private (sem modificador public).

Implementação: A classe ...RepositorioImpl deve ser public e implementar a interface da Camada de Aplicação.

Mapeamento: O mapeamento Domínio <-> JPA é feito via ModelMapper na classe JpaMapeador.

Crítico: Entidades de domínio imutáveis devem ser instanciadas via Provider/Converter usando o Construtor, nunca via reflexão/setters.

Camada de Apresentação (apresentacao):

Controllers: Use @RestController.

Injete apenas o Serviço de Aplicação.

Retorne ResponseEntity com os DTOs de Resumo.

Trate Optional com .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()).

Tarefa Atual: [DIGITE SUA SOLICITAÇÃO AQUI - Ex: Implementar a entidade Cliente com login e senha e seu repositório]