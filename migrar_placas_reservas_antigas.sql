-- ============================================================
-- Script SQL para migrar placa_veiculo de reservas antigas
-- Usa a placa do veículo da locação associada
-- ============================================================

-- Atualizar reservas antigas que têm locação associada
UPDATE RESERVA 
SET placa_veiculo = (
    SELECT veiculo_placa 
    FROM LOCACAO 
    WHERE LOCACAO.reserva_codigo = RESERVA.codigo
    LIMIT 1
)
WHERE placa_veiculo IS NULL 
    AND EXISTS (
        SELECT 1 
        FROM LOCACAO 
        WHERE LOCACAO.reserva_codigo = RESERVA.codigo
    );

-- Verificar quantas reservas ainda estão sem placa
SELECT 
    COUNT(*) as reservas_sem_placa,
    COUNT(CASE WHEN EXISTS (
        SELECT 1 FROM LOCACAO 
        WHERE LOCACAO.reserva_codigo = RESERVA.codigo
    ) THEN 1 END) as reservas_com_locacao_sem_placa
FROM RESERVA
WHERE placa_veiculo IS NULL;

-- Mostrar reservas que ainda não têm placa (se houver)
SELECT 
    codigo,
    categoria,
    cidade_retirada,
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM LOCACAO 
            WHERE LOCACAO.reserva_codigo = RESERVA.codigo
        ) THEN 'Tem locação (deveria ter placa)'
        ELSE 'Sem locação (OK)'
    END as status
FROM RESERVA
WHERE placa_veiculo IS NULL;

