-- ============================================================
-- Migração V9: Adicionar veículos em manutenção para testes
-- ============================================================

-- Atualizar alguns veículos existentes para EM_MANUTENCAO sem data prevista
-- Esses veículos aparecerão na lista de "precisam manutenção"

UPDATE VEICULO 
SET status = 'EM_MANUTENCAO',
    manutencao_prevista = NULL,
    manutencao_nota = NULL,
    patio_codigo = NULL,
    patio_localizacao = NULL
WHERE placa IN ('ABC1234', 'DEF5678', 'JKL3456', 'PQR1234', 'VWX9012');

