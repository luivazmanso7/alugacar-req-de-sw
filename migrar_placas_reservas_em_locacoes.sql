-- ============================================================
-- Script SQL para migrar placa_veiculo de reservas que estão
-- dentro de locações (usando a placa do veículo da locação)
-- ============================================================

-- Atualizar reservas antigas que estão dentro de locações
-- Usar a placa do veículo da locação como placa da reserva
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

-- Verificar quantas reservas foram atualizadas
SELECT COUNT(*) as reservas_atualizadas
FROM RESERVA
WHERE placa_veiculo IS NOT NULL
    AND EXISTS (
        SELECT 1 FROM LOCACAO 
        WHERE LOCACAO.reserva_codigo = RESERVA.codigo
    );

-- Verificar se ainda há reservas sem placa dentro de locações
SELECT COUNT(*) as reservas_sem_placa_em_locacoes
FROM RESERVA r
WHERE r.placa_veiculo IS NULL
    AND EXISTS (
        SELECT 1 FROM LOCACAO l
        WHERE l.reserva_codigo = r.codigo
    );

-- Mostrar reservas atualizadas
SELECT 
    r.codigo as reserva,
    r.placa_veiculo,
    l.codigo as locacao,
    l.veiculo_placa
FROM RESERVA r
JOIN LOCACAO l ON l.reserva_codigo = r.codigo
WHERE r.placa_veiculo IS NOT NULL
ORDER BY r.codigo;

