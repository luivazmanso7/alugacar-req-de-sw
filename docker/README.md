# Docker - AlugaCar

Este diretório contém a configuração do Docker para o projeto AlugaCar.

## Serviços Disponíveis

### PostgreSQL
- **Porta:** 5432
- **Banco de dados:** alugacar
- **Usuário:** alugacar_user
- **Senha:** alugacar_pass

### PgAdmin (Opcional)
- **Porta:** 5050
- **Email:** admin@alugacar.com
- **Senha:** admin

## Como Usar

### 1. Iniciar os serviços

```bash
# Na raiz do projeto
docker-compose up -d
```

### 2. Verificar o status dos containers

```bash
docker-compose ps
```

### 3. Ver logs dos containers

```bash
# Todos os logs
docker-compose logs -f

# Logs apenas do PostgreSQL
docker-compose logs -f postgres
```

### 4. Parar os serviços

```bash
docker-compose down
```

### 5. Parar e remover volumes (CUIDADO: apaga os dados!)

```bash
docker-compose down -v
```

## Executar a Aplicação com PostgreSQL

### Opção 1: Via Maven

```bash
# Na raiz do projeto
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

### Opção 2: Via IDE

Configure o profile ativo como `docker` nas configurações de execução.

### Opção 3: Via JAR

```bash
java -jar -Dspring.profiles.active=docker target/seu-app.jar
```

## Acessar o Banco de Dados

### Via PgAdmin (Interface Web)

1. Acesse: http://localhost:5050
2. Login: admin@alugacar.com / admin
3. Adicione um novo servidor:
   - Nome: AlugaCar
   - Host: postgres (ou localhost se estiver fora do Docker)
   - Port: 5432
   - Database: alugacar
   - Username: alugacar_user
   - Password: alugacar_pass

### Via psql (Linha de Comando)

```bash
# Conectar ao PostgreSQL
docker-compose exec postgres psql -U alugacar_user -d alugacar

# Ou usando psql local
psql -h localhost -p 5432 -U alugacar_user -d alugacar
```

### Via DBeaver / IntelliJ / Outra IDE

- Host: localhost
- Port: 5432
- Database: alugacar
- Username: alugacar_user
- Password: alugacar_pass

## Scripts de Inicialização

Os scripts SQL em `docker/init-db/` são executados automaticamente quando o container é criado pela primeira vez, em ordem alfabética.

## Variáveis de Ambiente

As configurações podem ser personalizadas editando o arquivo `.env` na raiz do projeto.

## Troubleshooting

### Porta já em uso

Se a porta 5432 já estiver em uso, altere no arquivo `.env`:

```
POSTGRES_PORT=5433
```

E reinicie os containers:

```bash
docker-compose down
docker-compose up -d
```

### Resetar o banco de dados

```bash
docker-compose down -v
docker-compose up -d
```

### Ver logs de erro

```bash
docker-compose logs postgres
```
