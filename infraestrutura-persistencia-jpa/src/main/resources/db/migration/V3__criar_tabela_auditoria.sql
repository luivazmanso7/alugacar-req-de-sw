-- Migration: Criar tabela de auditoria
-- Autor: Sistema
-- Data: 2024
-- Descrição: Cria a tabela 'auditoria' para armazenar histórico de eventos do sistema

CREATE TABLE auditoria (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    data_hora TIMESTAMP NOT NULL,
    operacao VARCHAR(100) NOT NULL,
    detalhes VARCHAR(500),
    usuario VARCHAR(100),
    
    INDEX idx_auditoria_operacao (operacao),
    INDEX idx_auditoria_data_hora (data_hora)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Comentários nas colunas
ALTER TABLE auditoria 
    MODIFY COLUMN id VARCHAR(36) COMMENT 'Identificador único UUID do registro de auditoria',
    MODIFY COLUMN data_hora TIMESTAMP COMMENT 'Data e hora em que o evento ocorreu',
    MODIFY COLUMN operacao VARCHAR(100) COMMENT 'Tipo da operação realizada (ex: LOCACAO_REALIZADA)',
    MODIFY COLUMN detalhes VARCHAR(500) COMMENT 'Detalhes adicionais sobre a operação',
    MODIFY COLUMN usuario VARCHAR(100) COMMENT 'Identificação do usuário ou sistema que executou a operação';

-- Comentário na tabela
ALTER TABLE auditoria COMMENT = 'Registros de auditoria para rastreabilidade de eventos do sistema';
