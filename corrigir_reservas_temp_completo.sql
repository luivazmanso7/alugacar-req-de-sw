-- Script completo para corrigir reservas com placa_veiculo = 'TEMP'
-- Respeita DDD: apenas corrige dados, não implementa lógica de negócio

-- 1. Atualizar reservas que têm locação associada
UPDATE RESERVA r
SET placa_veiculo = (
    SELECT l.veiculo_placa
    FROM LOCACAO l
    WHERE l.reserva_codigo = r.codigo
    LIMIT 1
)
WHERE r.placa_veiculo = 'TEMP'
  AND EXISTS (
      SELECT 1
      FROM LOCACAO l
      WHERE l.reserva_codigo = r.codigo
  );

-- 2. Para reservas ATIVAS sem locação, usar o primeiro veículo disponível da categoria
UPDATE RESERVA r
SET placa_veiculo = (
    SELECT v.placa
    FROM VEICULO v
    WHERE v.categoria = r.categoria
      AND v.cidade = r.cidade_retirada
      AND v.status = 'DISPONIVEL'
    LIMIT 1
)
WHERE r.placa_veiculo = 'TEMP'
  AND r.status = 'ATIVA'
  AND NOT EXISTS (
      SELECT 1
      FROM LOCACAO l
      WHERE l.reserva_codigo = r.codigo
  )
  AND EXISTS (
      SELECT 1
      FROM VEICULO v
      WHERE v.categoria = r.categoria
        AND v.cidade = r.cidade_retirada
        AND v.status = 'DISPONIVEL'
  );

-- 3. Para reservas CANCELADAS/EXPIRADAS sem locação, usar placa genérica
-- Isso permite que sejam listadas, mas indica que são dados antigos
UPDATE RESERVA
SET placa_veiculo = 'INVALIDA-' || SUBSTRING(codigo, 1, 8)
WHERE placa_veiculo = 'TEMP'
  AND status IN ('CANCELADA', 'EXPIRADA')
  AND NOT EXISTS (
      SELECT 1
      FROM LOCACAO l
      WHERE l.reserva_codigo = RESERVA.codigo
  );

-- 4. Verificar resultado
SELECT 
    COUNT(*) FILTER (WHERE placa_veiculo = 'TEMP') as ainda_temp,
    COUNT(*) FILTER (WHERE placa_veiculo LIKE 'INVALIDA-%') as invalidas,
    COUNT(*) FILTER (WHERE placa_veiculo NOT IN ('TEMP') AND placa_veiculo NOT LIKE 'INVALIDA-%') as corrigidas,
    COUNT(*) as total
FROM RESERVA;

