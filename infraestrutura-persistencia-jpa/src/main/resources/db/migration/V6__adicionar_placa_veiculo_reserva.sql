-- ============================================================
-- Migração V6: Adicionar campo placa_veiculo na tabela RESERVA
-- ============================================================

-- Adicionar coluna placa_veiculo na tabela RESERVA
-- Se a coluna já existir (criada pelo Hibernate), a migração será ignorada
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'reserva' AND column_name = 'placa_veiculo'
    ) THEN
        ALTER TABLE RESERVA ADD COLUMN placa_veiculo VARCHAR(10);
    END IF;
END $$;

-- Criar índice para melhorar performance de consultas por veículo
-- (Se já existir, será ignorado)
CREATE INDEX IF NOT EXISTS idx_reserva_placa_veiculo 
    ON RESERVA(placa_veiculo);

-- Adicionar constraint de foreign key para garantir integridade referencial
-- (Se já existir, será ignorado)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_reserva_veiculo' 
        AND table_name = 'reserva'
    ) THEN
        ALTER TABLE RESERVA
        ADD CONSTRAINT fk_reserva_veiculo 
            FOREIGN KEY (placa_veiculo) 
            REFERENCES VEICULO(placa);
    END IF;
END $$;

