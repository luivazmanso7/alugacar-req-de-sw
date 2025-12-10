-- Script de inicialização do banco de dados AlugaCar
-- Este script é executado automaticamente quando o container PostgreSQL é criado pela primeira vez

-- Criar extensões úteis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm"; -- Para busca de texto

-- Criar schema adicional se necessário (opcional)
-- CREATE SCHEMA IF NOT EXISTS auditoria;

-- Grant de permissões
GRANT ALL PRIVILEGES ON DATABASE alugacar TO alugacar_user;

-- Mensagem de confirmação
DO $$
BEGIN
  RAISE NOTICE 'Banco de dados AlugaCar inicializado com sucesso!';
END $$;
