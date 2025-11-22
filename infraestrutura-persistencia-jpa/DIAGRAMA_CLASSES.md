# Diagrama de Classes - Persistência JPA

## Modelo de Domínio → Modelo de Persistência

```
┌─────────────────────────────────────────────────────────────────┐
│                    CAMADA DE DOMÍNIO                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────────┐         ┌──────────────┐                    │
│   │   Veiculo    │         │   Cliente    │                    │
│   ├──────────────┤         ├──────────────┤                    │
│   │ - placa      │         │ - nome       │                    │
│   │ - modelo     │         │ - cpfOuCnpj  │                    │
│   │ - categoria  │         │ - cnh        │                    │
│   │ - cidade     │         │ - email      │                    │
│   │ - diaria     │         └──────────────┘                    │
│   │ - status     │                                             │
│   │ - patio      │         ┌──────────────┐                    │
│   └──────────────┘         │  Categoria   │                    │
│          ▲                 ├──────────────┤                    │
│          │                 │ - codigo     │                    │
│          │                 │ - nome       │                    │
│   ┌──────────────┐         │ - descricao  │                    │
│   │   Reserva    │         │ - diaria     │                    │
│   ├──────────────┤         └──────────────┘                    │
│   │ - codigo     │                                             │
│   │ - categoria  │         ┌──────────────┐                    │
│   │ - periodo    │         │   Locacao    │                    │
│   │ - cliente    │◆───────▶├──────────────┤                    │
│   └──────────────┘         │ - codigo     │                    │
│          ▲                 │ - reserva    │                    │
│          │                 │ - veiculo    │                    │
│          │                 │ - vistoria   │                    │
│          └─────────────────┘              │                    │
└─────────────────────────────────────────────────────────────────┘
                             │
                             │ Dependency Inversion
                             │ (Interface ↔ Implementação)
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│               CAMADA DE INFRAESTRUTURA (JPA)                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────────────┐         ┌──────────────────┐            │
│   │  VeiculoJpa      │         │  ClienteJpa      │            │
│   │  @Entity         │         │  @Entity         │            │
│   ├──────────────────┤         ├──────────────────┤            │
│   │ @Id              │         │ @Id              │            │
│   │ - placa: String  │         │ - cpfOuCnpj      │            │
│   │ - modelo         │         │ - nome           │            │
│   │ - categoria      │         │ - cnh            │            │
│   │ - cidade         │         │ - email          │            │
│   │ - diaria         │         └──────────────────┘            │
│   │ @Enumerated      │                                         │
│   │ - status         │         ┌──────────────────┐            │
│   │ @Embedded        │         │  CategoriaJpa    │            │
│   │ - patio          │         │  @Entity         │            │
│   └──────────────────┘         ├──────────────────┤            │
│          ▲                     │ @Id              │            │
│          │                     │ - codigo         │            │
│          │                     │ - nome           │            │
│   ┌──────────────────┐         │ - descricao      │            │
│   │  ReservaJpa      │         │ - diaria         │            │
│   │  @Entity         │         └──────────────────┘            │
│   ├──────────────────┤                                         │
│   │ @Id              │         ┌──────────────────┐            │
│   │ - codigo         │         │  LocacaoJpa      │            │
│   │ - categoria      │         │  @Entity         │            │
│   │ @Embedded        │         ├──────────────────┤            │
│   │ - periodo        │         │ @Id              │            │
│   │ @ManyToOne       │         │ - codigo         │            │
│   │ - cliente        │─────────▶│ @ManyToOne       │            │
│   └──────────────────┘         │ - reserva        │            │
│          ▲                     │ @ManyToOne       │            │
│          │                     │ - veiculo        │            │
│          │                     │ @Embedded        │            │
│          └─────────────────────│ - vistoria       │            │
│                                └──────────────────┘            │
│                                                                 │
│   ┌──────────────────────────────────────────────┐             │
│   │         Value Objects (@Embeddable)          │             │
│   ├──────────────────────────────────────────────┤             │
│   │                                              │             │
│   │  PatioJpa              PeriodoLocacaoJpa     │             │
│   │  - codigo              - retirada            │             │
│   │  - localizacao         - devolucao           │             │
│   │                                              │             │
│   │  ChecklistVistoriaJpa                        │             │
│   │  - quilometragem                             │             │
│   │  - combustivel                               │             │
│   │  - possuiAvarias                             │             │
│   └──────────────────────────────────────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                  BANCO DE DADOS RELACIONAL                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   VEICULO (placa PK, modelo, categoria, status, ...)           │
│   CLIENTE (cpf_cnpj PK, nome, cnh, email)                      │
│   CATEGORIA (codigo PK, nome, descricao, diaria, ...)          │
│   RESERVA (codigo PK, categoria, cliente FK, ...)              │
│   LOCACAO (codigo PK, reserva FK, veiculo FK, ...)             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Padrão de Implementação dos Repositórios

```
┌─────────────────────────────────────────────────────────────────┐
│              <<Interface>> VeiculoRepositorio                   │
│                    (Camada de Domínio)                          │
├─────────────────────────────────────────────────────────────────┤
│ + salvar(veiculo: Veiculo): void                               │
│ + buscarPorPlaca(placa: String): Optional<Veiculo>             │
│ + buscarDisponiveis(cidade, categoria): List<Veiculo>          │
└─────────────────────────────────────────────────────────────────┘
                             △
                             │ implements
                             │
┌─────────────────────────────────────────────────────────────────┐
│              VeiculoRepositorioImpl                             │
│              @Repository                                        │
│           (Camada de Infraestrutura)                            │
├─────────────────────────────────────────────────────────────────┤
│ - jpaRepository: VeiculoJpaRepository                           │
│ - modelMapper: ModelMapper                                      │
├─────────────────────────────────────────────────────────────────┤
│ + salvar(veiculo: Veiculo): void                               │
│   {                                                             │
│     veiculoJpa = modelMapper.map(veiculo, VeiculoJpa.class)    │
│     jpaRepository.save(veiculoJpa)                              │
│   }                                                             │
│                                                                 │
│ + buscarPorPlaca(placa: String): Optional<Veiculo>             │
│   {                                                             │
│     jpaOptional = jpaRepository.findById(placa)                 │
│     return jpaOptional.map(jpa ->                               │
│         modelMapper.map(jpa, Veiculo.class))                    │
│   }                                                             │
└─────────────────────────────────────────────────────────────────┘
                             │
                             │ uses
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│       <<Interface>> VeiculoJpaRepository                        │
│       extends JpaRepository<VeiculoJpa, String>                 │
│              (Spring Data JPA)                                  │
├─────────────────────────────────────────────────────────────────┤
│ + save(veiculoJpa): VeiculoJpa                                  │
│ + findById(id): Optional<VeiculoJpa>                            │
│ + findAll(): List<VeiculoJpa>                                   │
│ + findDisponiveisPorCidade(...): List<VeiculoJpa>               │
└─────────────────────────────────────────────────────────────────┘
```

## Fluxo de Conversão (Anti-Corruption Layer)

```
Cliente da API (Service/Controller)
         │
         │ usa
         ▼
   ┌─────────────────────────┐
   │  VeiculoRepositorio     │  ← Interface do domínio
   │  (interface)            │
   └─────────────────────────┘
         △
         │ implementa
         │
   ┌─────────────────────────┐
   │ VeiculoRepositorioImpl  │
   │ (@Repository)           │
   └─────────────────────────┘
         │
         │ (1) Recebe: Veiculo (domínio)
         │
         ▼
   ┌─────────────────────────┐
   │    ModelMapper          │  ← Converte Veiculo → VeiculoJpa
   └─────────────────────────┘
         │
         │ (2) Produz: VeiculoJpa (entidade JPA)
         │
         ▼
   ┌─────────────────────────┐
   │ VeiculoJpaRepository    │  ← Persiste no banco
   │ (Spring Data JPA)       │
   └─────────────────────────┘
         │
         │ (3) SQL: INSERT INTO VEICULO...
         │
         ▼
   ┌─────────────────────────┐
   │   Banco de Dados        │
   │   (PostgreSQL/H2)       │
   └─────────────────────────┘

   ← Retorno: mesmo fluxo invertido (VeiculoJpa → Veiculo)
```

## Anotações JPA Utilizadas

### Entidades
- `@Entity` - Marca classe como entidade JPA
- `@Table(name="...")` - Nome da tabela no banco
- `@Id` - Chave primária
- `@Column` - Configuração de coluna

### Relacionamentos
- `@ManyToOne` - Relacionamento muitos-para-um
- `@JoinColumn` - Coluna de junção (FK)
- `@Embedded` - Incorpora value object
- `@Embeddable` - Marca classe como incorporável

### Enums e Tipos
- `@Enumerated(EnumType.STRING)` - Enum persistido como String
- `@Temporal` - Tipos temporais (datas)

### Configurações
- `nullable = false` - Campo obrigatório
- `length = X` - Tamanho máximo
- `precision/scale` - Para BigDecimal

---

**Legenda**:
- `─▶` : Referência/Associação
- `◆─▶` : Composição
- `△` : Herança/Implementação
- `PK` : Primary Key
- `FK` : Foreign Key
