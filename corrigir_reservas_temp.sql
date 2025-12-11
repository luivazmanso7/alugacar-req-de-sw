-- Script para corrigir reservas com placa_veiculo = 'TEMP'
-- Tenta encontrar a placa correta através da locação associada

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

-- 2. Verificar quantas reservas ainda têm placa TEMP
SELECT 
    codigo,
    status,
    placa_veiculo,
    categoria,
    cidade_retirada,
    'AINDA_TEMP' as problema
FROM RESERVA
WHERE placa_veiculo = 'TEMP'
ORDER BY codigo;

-- 3. Para reservas canceladas sem locação, podemos usar uma placa genérica ou excluir da listagem
-- Opção 1: Usar placa genérica (não recomendado, mas permite listar)
-- UPDATE RESERVA SET placa_veiculo = 'INVALIDA' WHERE placa_veiculo = 'TEMP' AND status = 'CANCELADA';

-- Opção 2: Deletar reservas canceladas antigas sem placa válida (se não forem importantes)
-- DELETE FROM RESERVA WHERE placa_veiculo = 'TEMP' AND status = 'CANCELADA';

