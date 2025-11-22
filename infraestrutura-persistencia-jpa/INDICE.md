# 📦 Camada de Persistência JPA - AlugaCar
## Índice de Arquivos e Documentação

Este módulo implementa a **camada de persistência com mapeamento objeto-relacional** utilizando **JPA/Hibernate** seguindo os princípios de **DDD** e **Arquitetura Limpa**.

---

## 📚 Documentação

| Documento                  | Descrição                                            |
|----------------------------|------------------------------------------------------|
| **README.md**              | Documentação completa da arquitetura e conceitos     |
| **RESUMO_EXECUTIVO.md**    | Resumo do que foi implementado (para professores)    |
| **GUIA_RAPIDO.md**         | Guia prático de uso e exemplos de código            |
| **DIAGRAMA_CLASSES.md**    | Diagrama UML das classes e relacionamentos           |
| **INDICE.md**              | Este arquivo (navegação)                             |

---

## 🏗️ Estrutura de Código

### 1️⃣ Configuração

```
src/main/java/
└── dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa/
    └── JpaConfiguration.java              ✓ Configuração do ModelMapper
```

### 2️⃣ Entidades JPA (8 classes)

```
src/main/java/.../entities/
├── VeiculoJpa.java                        ✓ @Entity - Tabela VEICULO
├── ClienteJpa.java                        ✓ @Entity - Tabela CLIENTE
├── CategoriaJpa.java                      ✓ @Entity - Tabela CATEGORIA
├── ReservaJpa.java                        ✓ @Entity - Tabela RESERVA
├── LocacaoJpa.java                        ✓ @Entity - Tabela LOCACAO
├── PatioJpa.java                          ✓ @Embeddable - Value Object
├── PeriodoLocacaoJpa.java                 ✓ @Embeddable - Value Object
└── ChecklistVistoriaJpa.java              ✓ @Embeddable - Value Object
```

### 3️⃣ Repositórios Spring Data JPA (5 interfaces)

```
src/main/java/.../repositories/
├── VeiculoJpaRepository.java              ✓ extends JpaRepository
├── ClienteJpaRepository.java              ✓ extends JpaRepository
├── CategoriaJpaRepository.java            ✓ extends JpaRepository
├── ReservaJpaRepository.java              ✓ extends JpaRepository
└── LocacaoJpaRepository.java              ✓ extends JpaRepository
```

### 4️⃣ Implementações dos Repositórios do Domínio (5 classes)

```
src/main/java/.../impl/
├── VeiculoRepositorioImpl.java            ✓ implements VeiculoRepositorio
├── ClienteRepositorioImpl.java            ✓ implements ClienteRepositorio
├── CategoriaRepositorioImpl.java          ✓ implements CategoriaRepositorio
├── ReservaRepositorioImpl.java            ✓ implements ReservaRepositorio
└── LocacaoRepositorioImpl.java            ✓ implements LocacaoRepositorio
```

---

## 💾 Banco de Dados

### Migrações Flyway

```
src/main/resources/db/migration/
├── V1__criar_schema_inicial.sql           ✓ DDL (CREATE TABLE, INDEX)
└── V2__inserir_dados_iniciais.sql         ✓ DML (INSERT - seed data)
```

### Configuração

```
src/main/resources/
└── application.properties                 ✓ Configurações Spring/JPA/Flyway
```

---

## 🧪 Testes

```
src/test/java/.../jpa/
└── VeiculoRepositorioIntegrationTest.java ✓ Teste de integração JPA
```

---

## 📊 Estatísticas do Projeto

| Categoria                    | Quantidade |
|------------------------------|------------|
| **Entidades JPA**            | 8          |
| **Repositórios JPA**         | 5          |
| **Implementações**           | 5          |
| **Scripts SQL**              | 2          |
| **Testes**                   | 1          |
| **Arquivos de Configuração** | 2          |
| **Documentação**             | 5          |
| **TOTAL DE ARQUIVOS**        | **28**     |

---

## 🗺️ Mapa de Navegação Rápida

### 📖 Para Estudar a Arquitetura
1. Comece pelo **RESUMO_EXECUTIVO.md** → visão geral
2. Depois leia **README.md** → detalhes técnicos
3. Veja **DIAGRAMA_CLASSES.md** → estrutura visual

### 💻 Para Implementar/Usar
1. Leia **GUIA_RAPIDO.md** → exemplos práticos
2. Veja **application.properties** → configurações
3. Consulte os testes em `src/test/` → casos de uso reais

### 🔍 Para Entender o Código
1. Entidades JPA: `src/main/java/.../entities/`
2. Repositórios: `src/main/java/.../repositories/`
3. Implementações: `src/main/java/.../impl/`
4. Migrações SQL: `src/main/resources/db/migration/`

---

## 🎯 Checklist de Conformidade DDD + Clean Architecture

### ✅ Níveis DDD
- [x] **Preliminar**: Contextos delimitados (Catálogo, Reserva, Locação)
- [x] **Estratégico**: Separação domínio vs infraestrutura
- [x] **Tático**: Agregados, Entities, Value Objects, Repositories
- [x] **Operacional**: Persistência JPA, migrações Flyway

### ✅ Princípios SOLID
- [x] **SRP**: Cada classe tem uma responsabilidade única
- [x] **OCP**: Aberto para extensão (novas entidades/repos)
- [x] **LSP**: Implementações substituíveis
- [x] **ISP**: Interfaces segregadas (um repo por agregado)
- [x] **DIP**: Domínio não depende de infraestrutura

### ✅ Arquitetura Limpa
- [x] Domínio independente de frameworks
- [x] Infraestrutura implementa interfaces do domínio
- [x] Anti-Corruption Layer (ModelMapper)
- [x] Testabilidade (testes sem banco real)

---

## 📋 Tabelas do Banco de Dados

### Criadas em V1 (DDL)

| Tabela       | Chave Primária | Foreign Keys        | Descrição                |
|--------------|----------------|---------------------|--------------------------|
| `CATEGORIA`  | `codigo`       | -                   | Categorias de veículos   |
| `VEICULO`    | `placa`        | `categoria` → CATEGORIA | Frota de veículos    |
| `CLIENTE`    | `cpf_cnpj`     | -                   | Clientes cadastrados     |
| `RESERVA`    | `codigo`       | `cliente`, `categoria` | Reservas de locação   |
| `LOCACAO`    | `codigo`       | `reserva`, `veiculo` | Locações ativas/finalizadas |

### Populadas em V2 (DML)

- **5 categorias** (Econômico, Compacto, Intermediário, SUV, Luxo)
- **3 clientes** de exemplo
- **14 veículos** disponíveis (São Paulo: 9, Rio de Janeiro: 5)

---

## 🔗 Links Úteis

- **Domínio Principal**: `/dominio-principal/`
- **Repositórios do Domínio**: `/dominio-principal/src/main/java/.../repositorio/`
- **Console H2**: `http://localhost:8080/h2-console` (quando rodando)

---

## 📝 Notas para o Professor

Este módulo demonstra:

1. **Mapeamento ORM completo** com JPA/Hibernate
2. **Separação de responsabilidades** (domínio vs infraestrutura)
3. **Inversão de dependência** (interfaces no domínio)
4. **Padrão Repository** implementado corretamente
5. **Migrações versionadas** com Flyway
6. **Value Objects** como `@Embeddable`
7. **Relacionamentos JPA** (`@ManyToOne`, `@JoinColumn`)
8. **Anti-Corruption Layer** (ModelMapper)
9. **Testes de integração** com banco H2
10. **Documentação completa** e didática

Todos os **níveis do DDD** estão contemplados e a **Arquitetura Limpa** foi respeitada.

---

**Desenvolvido em**: Novembro de 2025  
**Tecnologias**: Java 17+, Spring Boot 3.x, JPA/Hibernate 6.x, Flyway  
**Padrões**: DDD, Clean Architecture, Repository Pattern  
