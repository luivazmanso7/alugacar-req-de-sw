# 🔐 Credenciais de Login

## Como rodar as migrações

As migrações são executadas **automaticamente** quando você inicia a aplicação Spring Boot. O Flyway executa todas as migrações na pasta `infraestrutura-persistencia-jpa/src/main/resources/db/migration/` na ordem numérica (V1, V2, V3, V4...).

### ⚠️ IMPORTANTE: Banco de Dados

A aplicação pode estar usando **PostgreSQL** (via Docker) ou **H2 em memória**:

- **PostgreSQL**: Migrações são executadas na primeira inicialização e depois apenas novas migrações são aplicadas
- **H2 em memória**: Migrações são executadas toda vez que a aplicação inicia (banco é recriado)

### Para rodar a aplicação:

```bash
# 1. Compilar tudo (opcional, mas recomendado)
mvn clean install -DskipTests

# 2. Iniciar aplicação
cd apresentacao-rest
mvn spring-boot:run
```

### Se estiver usando PostgreSQL via Docker:

```bash
# 1. Iniciar banco de dados
docker-compose up -d postgres

# 2. Aguardar banco ficar pronto (alguns segundos)

# 3. Iniciar aplicação
cd apresentacao-rest
mvn spring-boot:run
```

### Para forçar execução das migrações novamente (PostgreSQL):

Se as migrações já foram executadas e você quer reexecutá-las:

```bash
# Conectar ao banco e limpar histórico do Flyway
docker-compose exec postgres psql -U alugacar_user -d alugacar -c "DELETE FROM flyway_schema_history WHERE version >= '4';"

# Reiniciar aplicação (as migrações serão executadas novamente)
```

## 📋 Credenciais disponíveis

Todas as senhas são: **`senha123`**

| Login | Senha | Nome do Cliente |
|-------|-------|-----------------|
| `joao.silva` | `senha123` | João Silva |
| `maria.santos` | `senha123` | Maria Santos |
| `carlos.oliveira` | `senha123` | Carlos Oliveira |

## 🧪 Testando o login via API

### Via curl:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"joao.silva","senha":"senha123"}' \
  -c cookies.txt \
  -v
```

### Via frontend:

1. Acesse: `http://localhost:3000/alugar/login`
2. Use qualquer uma das credenciais acima
3. Após login, você será redirecionado para `/alugar`

## 🔍 Verificar se as migrações foram executadas

Os logs do Spring Boot mostram quando as migrações são executadas. Procure por linhas como:

```
Flyway: Successfully applied X migration(s)
```

## ✅ Status Atual

**Login está funcionando!** Os clientes foram atualizados no banco de dados PostgreSQL.

## ⚠️ Problemas comuns

1. **Migrações não executam**: 
   - Verifique se o Flyway está habilitado no `application.properties` (deve estar `spring.flyway.enabled=true`)
   - Se estiver usando PostgreSQL e as migrações já foram executadas, elas não serão reexecutadas automaticamente
   - Para executar manualmente: `docker-compose exec postgres psql -U alugacar_user -d alugacar` e executar os scripts SQL

2. **Login não funciona**: 
   - Verifique se o PostgreSQL está rodando: `docker-compose ps postgres`
   - Verifique se os clientes têm login/senha no banco
   - Verifique se o hash está correto: `HASH_1251475389` para senha "senha123"

3. **Hash incorreto**: O hash é calculado como `"HASH_" + senha.hashCode()`. Para "senha123", o hash é `HASH_1251475389`

## 🔧 Executar migrações manualmente (se necessário)

Se o Flyway não executar automaticamente, você pode executar manualmente:

```bash
docker-compose exec postgres psql -U alugacar_user -d alugacar -f /caminho/para/migracao.sql
```

Ou executar diretamente:

```bash
docker-compose exec postgres psql -U alugacar_user -d alugacar
```

E então executar os comandos SQL da migração V4.

