-- Script para verificar reservas com dados inválidos
-- Execute este script para identificar reservas que podem causar erros no ModelMapper

-- 1. Reservas sem placa_veiculo ou com placa inválida
SELECT 
    codigo,
    status,
    placa_veiculo,
    categoria,
    cidade_retirada,
    'SEM_PLACA_VALIDA' as problema
FROM RESERVA
WHERE placa_veiculo IS NULL 
   OR placa_veiculo = '' 
   OR placa_veiculo = 'MIGRAR' 
   OR placa_veiculo = 'TEMP'
ORDER BY codigo;

-- 2. Reservas sem período
SELECT 
    codigo,
    status,
    placa_veiculo,
    'SEM_PERIODO' as problema
FROM RESERVA
WHERE periodo_retirada IS NULL 
   OR periodo_devolucao IS NULL
ORDER BY codigo;

-- 3. Reservas sem cliente
SELECT 
    r.codigo,
    r.status,
    r.placa_veiculo,
    r.cliente_cpf_cnpj,
    'SEM_CLIENTE' as problema
FROM RESERVA r
LEFT JOIN CLIENTE c ON r.cliente_cpf_cnpj = c.cpf_ou_cnpj
WHERE r.cliente_cpf_cnpj IS NULL 
   OR c.cpf_ou_cnpj IS NULL
ORDER BY r.codigo;

-- 4. Reservas sem categoria
SELECT 
    codigo,
    status,
    categoria,
    'SEM_CATEGORIA' as problema
FROM RESERVA
WHERE categoria IS NULL 
   OR categoria = ''
ORDER BY codigo;

-- 5. Reservas sem valor estimado
SELECT 
    codigo,
    status,
    valor_estimado,
    'SEM_VALOR_ESTIMADO' as problema
FROM RESERVA
WHERE valor_estimado IS NULL
ORDER BY codigo;

-- 6. Reservas sem status
SELECT 
    codigo,
    status,
    'SEM_STATUS' as problema
FROM RESERVA
WHERE status IS NULL
ORDER BY codigo;

-- RESUMO: Contar quantas reservas têm problemas
SELECT 
    COUNT(*) FILTER (WHERE placa_veiculo IS NULL OR placa_veiculo = '' OR placa_veiculo = 'MIGRAR' OR placa_veiculo = 'TEMP') as sem_placa_valida,
    COUNT(*) FILTER (WHERE periodo_retirada IS NULL OR periodo_devolucao IS NULL) as sem_periodo,
    COUNT(*) FILTER (WHERE categoria IS NULL OR categoria = '') as sem_categoria,
    COUNT(*) FILTER (WHERE valor_estimado IS NULL) as sem_valor,
    COUNT(*) FILTER (WHERE status IS NULL) as sem_status,
    COUNT(*) as total_reservas
FROM RESERVA;

