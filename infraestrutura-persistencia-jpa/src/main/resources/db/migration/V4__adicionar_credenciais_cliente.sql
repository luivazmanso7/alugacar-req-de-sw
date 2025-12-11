-- ============================================================
-- Migração V4: Adicionar credenciais e status aos clientes
-- ============================================================

-- Adicionar colunas de autenticação
ALTER TABLE CLIENTE ADD COLUMN login VARCHAR(30) NOT NULL DEFAULT 'temp_login';
ALTER TABLE CLIENTE ADD COLUMN senha_hash VARCHAR(100) NOT NULL DEFAULT 'HASH_0';
ALTER TABLE CLIENTE ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ATIVO';

-- Criar índice único para login
CREATE UNIQUE INDEX idx_cliente_login ON CLIENTE(login);

-- Atualizar clientes existentes com logins e senhas
-- Hash calculado: "senha123".hashCode() = 1251475389
UPDATE CLIENTE SET 
    login = 'joao.silva',
    senha_hash = 'HASH_1251475389',  -- senha: senha123
    status = 'ATIVO'
WHERE cpf_cnpj = '12345678901';

UPDATE CLIENTE SET 
    login = 'maria.santos',
    senha_hash = 'HASH_1251475389',  -- senha: senha123
    status = 'ATIVO'
WHERE cpf_cnpj = '98765432100';

UPDATE CLIENTE SET 
    login = 'carlos.oliveira',
    senha_hash = 'HASH_1251475389',  -- senha: senha123
    status = 'ATIVO'
WHERE cpf_cnpj = '45678912300';

-- Remover valores default após migração
ALTER TABLE CLIENTE ALTER COLUMN login DROP DEFAULT;
ALTER TABLE CLIENTE ALTER COLUMN senha_hash DROP DEFAULT;
ALTER TABLE CLIENTE ALTER COLUMN status DROP DEFAULT;
