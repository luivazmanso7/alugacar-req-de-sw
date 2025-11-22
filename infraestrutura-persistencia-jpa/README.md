# Camada de Persistência JPA - AlugaCar

## Visão Geral

Este módulo implementa a **camada de persistência** do sistema AlugaCar utilizando **JPA (Java Persistence API)** com **Hibernate** e **Spring Data JPA**, seguindo os princípios de **DDD (Domain-Driven Design)** e **Arquitetura Limpa**.

## Arquitetura

A implementação segue a separação de responsabilidades em camadas:

```
┌─────────────────────────────────────────┐
│         Camada de Domínio               │
│  (Entidades, Value Objects, Agregados)  │
│     Interfaces de Repositório           │
└─────────────────┬───────────────────────┘
                  │
                  │ Dependency Inversion
                  ▼
┌─────────────────────────────────────────┐
│    Camada de Infraestrutura (JPA)       │
│  ┌───────────────────────────────────┐  │
│  │  Entidades JPA (@Entity)          │  │
│  │  - VeiculoJpa, ClienteJpa, etc    │  │
│  └───────────────────────────────────┘  │
│  ┌───────────────────────────────────┐  │
│  │  Repositórios JPA (Spring Data)   │  │
│  │  - VeiculoJpaRepository, etc      │  │
│  └───────────────────────────────────┘  │
│  ┌───────────────────────────────────┐  │
│  │  Implementações dos Repositórios  │  │
│  │  - VeiculoRepositorioImpl         │  │
│  │  (converte JPA ↔ Domínio)         │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

## Estrutura de Pacotes

```
dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa
├── JpaConfiguration.java              # Configuração do ModelMapper
├── entities/                           # Entidades JPA
│   ├── VeiculoJpa.java
│   ├── ClienteJpa.java
│   ├── CategoriaJpa.java
│   ├── ReservaJpa.java
│   ├── LocacaoJpa.java
│   ├── PatioJpa.java                   # @Embeddable
│   ├── PeriodoLocacaoJpa.java          # @Embeddable
│   └── ChecklistVistoriaJpa.java       # @Embeddable
├── repositories/                       # Spring Data JPA Repositories
│   ├── VeiculoJpaRepository.java
│   ├── ClienteJpaRepository.java
│   ├── CategoriaJpaRepository.java
│   ├── ReservaJpaRepository.java
│   └── LocacaoJpaRepository.java
└── impl/                               # Implementações dos Repositórios do Domínio
    ├── VeiculoRepositorioImpl.java
    ├── ClienteRepositorioImpl.java
    ├── CategoriaRepositorioImpl.java
    ├── ReservaRepositorioImpl.java
    └── LocacaoRepositorioImpl.java
```

## Mapeamento Objeto-Relacional

### Agregados e Entidades

| Domínio            | Entidade JPA           | Tabela      | Chave Primária |
|--------------------|------------------------|-------------|----------------|
| `Veiculo`          | `VeiculoJpa`           | `VEICULO`   | `placa`        |
| `Cliente`          | `ClienteJpa`           | `CLIENTE`   | `cpf_cnpj`     |
| `Categoria`        | `CategoriaJpa`         | `CATEGORIA` | `codigo`       |
| `Reserva`          | `ReservaJpa`           | `RESERVA`   | `codigo`       |
| `Locacao`          | `LocacaoJpa`           | `LOCACAO`   | `codigo`       |

### Value Objects (Embeddable)

| Domínio               | JPA                       | Incorporado em    |
|-----------------------|---------------------------|-------------------|
| `Patio`               | `PatioJpa`                | `VeiculoJpa`      |
| `PeriodoLocacao`      | `PeriodoLocacaoJpa`       | `ReservaJpa`      |
| `ChecklistVistoria`   | `ChecklistVistoriaJpa`    | `LocacaoJpa`      |

### Relacionamentos

- **Reserva → Cliente**: `@ManyToOne` (uma reserva pertence a um cliente)
- **Locacao → Reserva**: `@ManyToOne` (uma locação está vinculada a uma reserva)
- **Locacao → Veiculo**: `@ManyToOne` (uma locação utiliza um veículo)

## Migrações de Banco de Dados (Flyway)

As migrações estão localizadas em `src/main/resources/db/migration/`:

- **V1__criar_schema_inicial.sql**: Criação das tabelas e índices
- **V2__inserir_dados_iniciais.sql**: Dados de seed (categorias, clientes e veículos de exemplo)

## Tecnologias Utilizadas

- **JPA 3.0** (Jakarta Persistence API)
- **Hibernate 6.x** (implementação JPA)
- **Spring Data JPA** (abstração de repositórios)
- **Flyway** (controle de versão do banco de dados)
- **ModelMapper** (conversão entre entidades de domínio e JPA)
- **H2 Database** (banco em memória para desenvolvimento)
- **PostgreSQL** (banco para produção)

## Princípios Aplicados

### 1. **Dependency Inversion Principle (DIP)**
- As interfaces dos repositórios estão no **domínio**
- A **infraestrutura** implementa essas interfaces
- O domínio **não conhece** a infraestrutura

### 2. **Separation of Concerns**
- Entidades JPA são separadas das entidades de domínio
- ModelMapper faz a conversão (anti-corruption layer)
- Domínio permanece livre de anotações JPA

### 3. **Repository Pattern**
- Abstração de acesso aos dados
- Implementações específicas de persistência isoladas

## Como Usar

### 1. Adicionar como Dependência

```xml
<dependency>
    <groupId>dev.sauloaraujo.alugacar</groupId>
    <artifactId>alugacar-infraestrutura-persistencia-jpa</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Configurar Spring Boot Application

```java
@SpringBootApplication
@EnableJpaRepositories(basePackages = "dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa")
public class AlugaCarApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlugaCarApplication.class, args);
    }
}
```

### 3. Injetar Repositórios

```java
@Service
public class VeiculoService {
    
    @Autowired
    private VeiculoRepositorio veiculoRepositorio; // Interface do domínio
    
    public void cadastrarVeiculo(Veiculo veiculo) {
        veiculoRepositorio.salvar(veiculo);
    }
}
```

## Exemplos de Consultas

### Buscar Veículos Disponíveis

```java
List<Veiculo> veiculos = veiculoRepositorio.buscarDisponiveis(
    "São Paulo", 
    CategoriaCodigo.ECONOMICO
);
```

### Buscar Cliente por Documento

```java
Optional<Cliente> cliente = clienteRepositorio.buscarPorDocumento("12345678901");
```

### Listar Todas as Categorias

```java
List<Categoria> categorias = categoriaRepositorio.listarTodas();
```

## Testes

Para executar os testes de integração da camada de persistência:

```bash
mvn test
```

## Considerações de Performance

- **Índices**: Criados para consultas frequentes (cidade, categoria, status)
- **Lazy Loading**: Relacionamentos carregados sob demanda
- **Batch Operations**: Suportado pelo Spring Data JPA
- **Connection Pool**: Configurado via HikariCP (padrão do Spring Boot)

## Autor

Desenvolvido seguindo os padrões DDD e Clean Architecture para o projeto AlugaCar.
