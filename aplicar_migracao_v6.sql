-- ============================================================
-- Script SQL para aplicar Migração V6 manualmente
-- Execute este arquivo no PostgreSQL
-- ============================================================

-- Adicionar coluna placa_veiculo na tabela RESERVA
ALTER TABLE RESERVA ADD COLUMN IF NOT EXISTS placa_veiculo VARCHAR(10);

-- Criar índice para melhorar performance
CREATE INDEX IF NOT EXISTS idx_reserva_placa_veiculo 
    ON RESERVA(placa_veiculo);

-- Adicionar foreign key (descomente se quiser adicionar a constraint)
-- ALTER TABLE RESERVA
-- ADD CONSTRAINT fk_reserva_veiculo 
--     FOREIGN KEY (placa_veiculo) 
--     REFERENCES VEICULO(placa);

-- Verificar se foi aplicado
SELECT 
    column_name, 
    data_type, 
    is_nullable,
    character_maximum_length
FROM information_schema.columns 
WHERE table_name = 'reserva' 
    AND column_name = 'placa_veiculo';

