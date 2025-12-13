-- ============================================================
-- Script de Seed: Criar usuários padrão (Administrador e Cliente)
-- ============================================================
-- Este script cria um administrador e um cliente padrão para testes
-- Execute este script ANTES de iniciar a aplicação pela primeira vez
-- 
-- Credenciais padrão:
-- Administrador:
--   Login: admin
--   Senha: admin123
--
-- Cliente:
--   Login: cliente
--   Senha: cliente123
-- ============================================================

-- Garantir que a tabela ADMINISTRADOR existe
CREATE TABLE IF NOT EXISTS ADMINISTRADOR (
    id VARCHAR(50) PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    email VARCHAR(150) NOT NULL,
    login VARCHAR(30) NOT NULL UNIQUE,
    senha_hash VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO'
);

CREATE INDEX IF NOT EXISTS idx_administrador_login ON ADMINISTRADOR(login);

-- Garantir que a tabela CLIENTE existe com todas as colunas necessárias
CREATE TABLE IF NOT EXISTS CLIENTE (
    cpf_cnpj VARCHAR(14) PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    cnh VARCHAR(11) NOT NULL,
    email VARCHAR(150) NOT NULL,
    login VARCHAR(30),
    senha_hash VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ATIVO'
);

-- Adicionar colunas de autenticação se não existirem
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'cliente' AND column_name = 'login') THEN
        ALTER TABLE CLIENTE ADD COLUMN login VARCHAR(30);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'cliente' AND column_name = 'senha_hash') THEN
        ALTER TABLE CLIENTE ADD COLUMN senha_hash VARCHAR(100);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'cliente' AND column_name = 'status') THEN
        ALTER TABLE CLIENTE ADD COLUMN status VARCHAR(20) DEFAULT 'ATIVO';
    END IF;
END $$;

-- Criar índice único para login do cliente se não existir
CREATE UNIQUE INDEX IF NOT EXISTS idx_cliente_login ON CLIENTE(login) WHERE login IS NOT NULL;

-- Criar Administrador padrão
-- Hash da senha "admin123": calculado usando hashCode() do Java
INSERT INTO ADMINISTRADOR (id, nome, email, login, senha_hash, status) 
VALUES (
    'admin-001',
    'Administrador Principal',
    'admin@alugacar.com',
    'admin',
    'HASH_-969161597',  -- senha: admin123
    'ATIVO'
)
ON CONFLICT (id) DO UPDATE SET
    nome = EXCLUDED.nome,
    email = EXCLUDED.email,
    login = EXCLUDED.login,
    senha_hash = EXCLUDED.senha_hash,
    status = EXCLUDED.status;

-- Criar Cliente padrão
-- Hash da senha "cliente123": calculado usando hashCode() do Java
INSERT INTO CLIENTE (cpf_cnpj, nome, cnh, email, login, senha_hash, status)
VALUES (
    '12345678901',
    'Cliente Teste',
    '12345678901',
    'cliente@alugacar.com',
    'cliente',
    'HASH_1102888440',  -- senha: cliente123
    'ATIVO'
)
ON CONFLICT (cpf_cnpj) DO UPDATE SET
    nome = EXCLUDED.nome,
    cnh = EXCLUDED.cnh,
    email = EXCLUDED.email,
    login = EXCLUDED.login,
    senha_hash = EXCLUDED.senha_hash,
    status = EXCLUDED.status;

-- Mensagem de confirmação
DO $$
BEGIN
  RAISE NOTICE 'Usuários padrão criados com sucesso!';
  RAISE NOTICE 'Administrador - Login: admin | Senha: admin123';
  RAISE NOTICE 'Cliente - Login: cliente | Senha: cliente123';
END $$;

