-- ============================================================
-- Migração V8: Adicionar status EM_ANDAMENTO para LOCACAO
-- ============================================================

-- Remove a constraint antiga se existir
ALTER TABLE LOCACAO DROP CONSTRAINT IF EXISTS locacao_status_check;

-- Adiciona a nova constraint que permite ATIVA, EM_ANDAMENTO e FINALIZADA
ALTER TABLE LOCACAO 
ADD CONSTRAINT locacao_status_check 
CHECK (status IN ('ATIVA', 'EM_ANDAMENTO', 'FINALIZADA'));

