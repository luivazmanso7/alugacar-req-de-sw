-- ============================================================
-- Migração V5: Criar cliente "lui" para testes
-- ============================================================

-- Inserir novo cliente "lui"
-- Hash calculado: "123".hashCode() = 48690
INSERT INTO CLIENTE (cpf_cnpj, nome, cnh, email, login, senha_hash, status)
VALUES (
    '11122233344',           -- CPF
    'Lui Manso',            -- Nome
    '11122233344',          -- CNH (mesmo do CPF)
    'lui@example.com',      -- Email
    'lui',                  -- Login
    'HASH_48690',           -- Senha: 123 (hashCode = 48690)
    'ATIVO'                 -- Status
)
ON CONFLICT (cpf_cnpj) DO UPDATE SET
    login = EXCLUDED.login,
    senha_hash = EXCLUDED.senha_hash,
    status = EXCLUDED.status;

-- Se o login já existir em outro cliente, atualizar
UPDATE CLIENTE 
SET login = 'lui_' || cpf_cnpj
WHERE login = 'lui' AND cpf_cnpj != '11122233344';

