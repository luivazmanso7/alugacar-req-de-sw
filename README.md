# Sistema AlugaCar

Sistema de Gestão de Locação de Veículos desenvolvido com Domain-Driven Design (DDD) e Arquitetura Limpa. O sistema permite gerenciar o ciclo completo de locação de veículos, desde o catálogo até o faturamento.

## Estrutura do Projeto

O projeto é organizado em módulos Maven seguindo os princípios da Arquitetura Limpa:

- `dominio-principal`: Camada de domínio com entidades, value objects e serviços de domínio
- `aplicacao-locacao`: Camada de aplicação com serviços de aplicação e DTOs
- `infraestrutura-persistencia-jpa`: Camada de infraestrutura com implementações JPA
- `apresentacao-rest`: Camada de apresentação com controllers REST e frontend Next.js

## Pré-requisitos

- Java 17 ou superior
- Maven 3.8 ou superior
- Node.js 18 ou superior
- Docker e Docker Compose (para o banco de dados PostgreSQL)
- PostgreSQL 15 (opcional, se não usar Docker)

## Configuração do Banco de Dados

### Opção 1: Usando Docker Compose (Recomendado)

O projeto inclui um arquivo `docker-compose.yml` que configura automaticamente o PostgreSQL e o PgAdmin. Os scripts de inicialização em `docker/init-db/` são executados automaticamente na primeira inicialização do container.

**IMPORTANTE**: Antes de iniciar o Docker, certifique-se de que os scripts de inicialização estão no lugar. O script `02-seed-usuarios.sql` cria automaticamente um administrador e um cliente padrão.

```bash
# Iniciar os containers
docker-compose up -d

# Verificar se os containers estão rodando
docker-compose ps

# Verificar os logs para confirmar a execução dos scripts
docker-compose logs postgres

# Parar os containers
docker-compose down
```

O banco de dados será criado automaticamente com as seguintes configurações padrão:
- **Host**: localhost
- **Porta**: 5432
- **Database**: alugacar
- **Usuário**: alugacar_user
- **Senha**: alugacar_pass

### Usuários Padrão Criados Automaticamente

O script `docker/init-db/02-seed-usuarios.sql` cria automaticamente os seguintes usuários:

**Administrador:**
- Login: `admin`
- Senha: `admin123`
- Email: admin@alugacar.com

**Cliente:**
- Login: `cliente`
- Senha: `cliente123`
- CPF: 12345678901
- Email: cliente@alugacar.com

Estes usuários são criados automaticamente quando o container PostgreSQL é iniciado pela primeira vez. Se você precisar recriar os usuários, pode executar o script manualmente ou recriar o volume do banco de dados:

```bash
# Remover o volume e recriar (ATENÇÃO: isso apaga todos os dados)
docker-compose down -v
docker-compose up -d
```

### Executando o Script de Seed Manualmente

Se você estiver usando PostgreSQL local ou precisar recriar os usuários padrão, execute o script manualmente:

```bash
# Conectar ao banco de dados
psql -h localhost -U alugacar_user -d alugacar

# Executar o script
\i docker/init-db/02-seed-usuarios.sql
```

Ou usando psql diretamente:

```bash
psql -h localhost -U alugacar_user -d alugacar -f docker/init-db/02-seed-usuarios.sql
```

### Opção 2: PostgreSQL Local

Se preferir usar um PostgreSQL local, crie o banco de dados manualmente:

```sql
CREATE DATABASE alugacar;
CREATE USER alugacar_user WITH PASSWORD 'alugacar_pass';
GRANT ALL PRIVILEGES ON DATABASE alugacar TO alugacar_user;
```

Depois, execute os scripts de inicialização em ordem:

1. `docker/init-db/01-init.sql` - Cria extensões e configura permissões
2. `docker/init-db/02-seed-usuarios.sql` - Cria usuários padrão (administrador e cliente)

E atualize as configurações em `apresentacao-rest/src/main/resources/application.properties` se necessário.

## Executando o Backend

### 1. Compilar o projeto

Na raiz do projeto, execute:

```bash
mvn clean install
```

### 2. Executar a aplicação Spring Boot

```bash
cd apresentacao-rest
mvn spring-boot:run
```

Ou execute diretamente a classe principal:

```bash
cd apresentacao-rest
mvn exec:java -Dexec.mainClass="dev.sauloaraujo.sgb.apresentacao.AlugaCarApplication"
```

A aplicação estará disponível em:
- **API REST**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **API Docs**: http://localhost:8080/api/v1/api-docs

## Executando o Frontend

### 1. Instalar dependências

```bash
cd apresentacao-rest/frontend
npm install
```

### 2. Executar em modo de desenvolvimento

```bash
npm run dev
```

O frontend estará disponível em: http://localhost:3000

### 3. Build para produção

```bash
npm run build
npm start
```

## Executando os Testes

### Testes de Domínio (Cucumber)

Os testes BDD estão localizados em `dominio-principal/src/test`:

```bash
cd dominio-principal
mvn test
```

Ou execute todos os testes do projeto:

```bash
mvn test
```

### Testes Unitários

Para executar testes unitários de um módulo específico:

```bash
cd <modulo>
mvn test
```

## Configurações

### Variáveis de Ambiente (Docker)

Você pode personalizar as configurações do Docker através de variáveis de ambiente ou criando um arquivo `.env`:

```env
POSTGRES_DB=alugacar
POSTGRES_USER=alugacar_user
POSTGRES_PASSWORD=alugacar_pass
POSTGRES_PORT=5432
PGADMIN_EMAIL=admin@alugacar.com
PGADMIN_PASSWORD=admin
PGADMIN_PORT=5050
```

### Configurações da Aplicação

As configurações principais estão em `apresentacao-rest/src/main/resources/application.properties`:

- Porta do servidor: 8080
- Context path: /api/v1
- Banco de dados: PostgreSQL
- JPA: Hibernate com DDL auto-update

## Funcionalidades Implementadas

- Criar reserva de veículo
- Processar devolução e faturamento
- Confirmar retirada e gerar contrato
- Agendar manutenção de veículo
- Cancelar reserva
- Alterar reserva

## Padrões de Projeto Adotados

O sistema implementa os seguintes padrões de projeto:

1. **Strategy Pattern**: Cálculo flexível de multas por atraso
2. **Observer Pattern**: Eventos de domínio para manutenção de veículos
3. **Proxy Pattern**: Cache de reservas
4. **Iterator Pattern**: Processamento de coleções via Streams API
5. **Template Method Pattern**: Base para testes de funcionalidades

Para mais detalhes, consulte o arquivo `padroes.md`.

## Acessando o PgAdmin (Opcional)

Se você iniciou os containers com Docker Compose, o PgAdmin estará disponível em:

- **URL**: http://localhost:5050
- **Email**: admin@alugacar.com
- **Senha**: admin

Para conectar ao banco de dados no PgAdmin:
- **Host**: postgres (nome do serviço no Docker)
- **Porta**: 5432
- **Database**: alugacar
- **Usuário**: alugacar_user
- **Senha**: alugacar_pass

## Estrutura de Endpoints da API

### Autenticação
- `POST /api/v1/auth/login` - Login de cliente
- `POST /api/v1/auth/logout` - Logout
- `POST /api/v1/admin/auth/login` - Login de administrador

### Reservas
- `POST /api/v1/reservas` - Criar reserva
- `GET /api/v1/reservas/{codigo}` - Buscar reserva por código
- `GET /api/v1/reservas/minhas` - Listar reservas do cliente autenticado
- `PUT /api/v1/reservas/{codigo}` - Alterar reserva
- `DELETE /api/v1/reservas/{codigo}` - Cancelar reserva
- `POST /api/v1/reservas/{codigo}/retirada` - Confirmar retirada

### Veículos e Categorias
- `GET /api/v1/veiculos` - Listar veículos disponíveis
- `GET /api/v1/categorias` - Listar categorias

### Manutenção
- `POST /api/v1/veiculos/{placa}/manutencao` - Agendar manutenção
- `GET /api/v1/veiculos/manutencao` - Listar veículos que precisam de manutenção

### Locações (Admin)
- `POST /api/v1/admin/locacoes/{codigo}/processar-devolucao` - Processar devolução

Para documentação completa da API, acesse o Swagger UI em http://localhost:8080/api/v1/swagger-ui.html

## Troubleshooting

### Erro de conexão com o banco de dados

Verifique se o PostgreSQL está rodando:

```bash
docker-compose ps
```

Se não estiver rodando, inicie os containers:

```bash
docker-compose up -d
```

### Porta já em uso

Se a porta 8080 estiver em uso, altere em `apresentacao-rest/src/main/resources/application.properties`:

```properties
server.port=8081
```

### Erro ao compilar o projeto

Certifique-se de que está usando Java 17 ou superior:

```bash
java -version
```

E Maven 3.8 ou superior:

```bash
mvn -version
```

## Desenvolvimento

### Estrutura de Branches

- `main`: Código estável e testado
- `develop`: Desenvolvimento ativo

### Contribuindo

1. Crie uma branch a partir de `develop`
2. Faça suas alterações
3. Execute os testes: `mvn test`
4. Certifique-se de que o código compila: `mvn clean install`
5. Faça commit e push
6. Abra um Pull Request

## Licença

Este projeto está sob a licença especificada no arquivo LICENSE.
