-- ============================================================
-- Script de Seed de Dados - SGB Locação
-- Popula CLIENTE, CATEGORIA, VEICULO, RESERVA, LOCACAO, auditoria
-- Pode ser executado múltiplas vezes (usa ON CONFLICT DO NOTHING)
-- Banco alvo: PostgreSQL (schema criado pelas migrações V1..V4)
-- ============================================================

-- -----------------------------
-- 1) Categorias
-- -----------------------------
INSERT INTO CATEGORIA (codigo, nome, descricao, diaria, modelos_exemplo, quantidade_disponivel) VALUES
  ('ECONOMICO',    'Econômico',    'Veículos compactos e econômicos, ideais para a cidade',  89.90,  'Gol, Onix, HB20',                20),
  ('EXECUTIVO',    'Executivo',    'Carros de tamanho médio com bom espaço interno',        129.90, 'Civic, Corolla, Cruze',          15),
  ('INTERMEDIARIO','Intermediário','Veículos com mais conforto e espaço',                   159.90, 'Compass, Kicks, T-Cross',       10),
  ('SUV',          'SUV',          'Utilitários esportivos com amplo espaço',               249.90, 'Tucson, Tiguan, CR-V',            8),
  ('PREMIUM',      'Premium',      'Veículos premium com alto nível de conforto',          399.90, 'BMW X3, Audi Q5, Mercedes GLC',   5)
ON CONFLICT (codigo) DO NOTHING;


-- -----------------------------
-- 2) Clientes
-- -----------------------------
-- Observação: V4 adiciona login, senha_hash, status (sem default).
-- Usamos HASH_84970715 como senha fictícia para "senha123".
INSERT INTO CLIENTE (cpf_cnpj, nome, cnh, email, login, senha_hash, status) VALUES
  ('12345678901', 'João Silva',        '12345678901', 'joao.silva@example.com',   'joao.silva',   'HASH_84970715', 'ATIVO'),
  ('98765432100', 'Maria Santos',      '98765432100', 'maria.santos@example.com', 'maria.santos', 'HASH_84970715', 'ATIVO'),
  ('45678912300', 'Carlos Oliveira',   '45678912300', 'carlos.oliveira@example.com', 'carlos.oliveira', 'HASH_84970715', 'ATIVO'),
  ('11122233344', 'Ana Cliente',       '11122233344', 'ana.cliente@example.com', 'ana.cliente',  'HASH_84970715', 'ATIVO'),
  ('55566677788', 'Empresa XPTO Ltda', '55566677788', 'contato@xpto.com',        'xpto.contato', 'HASH_84970715', 'ATIVO')
ON CONFLICT (cpf_cnpj) DO NOTHING;


-- -----------------------------
-- 3) Veículos
-- -----------------------------
-- Exemplos em São Paulo e Rio, com diferentes categorias.
INSERT INTO VEICULO (placa, modelo, categoria, cidade, diaria, status, manutencao_prevista, manutencao_nota, patio_codigo, patio_localizacao) VALUES
  ('ABC1001', 'Volkswagen Gol 1.0',     'ECONOMICO',    'São Paulo',     89.90,  'DISPONIVEL', NULL, NULL, 'PATIO-SP-01', 'São Paulo'),
  ('DEF2002', 'Chevrolet Onix 1.0',     'ECONOMICO',    'São Paulo',     89.90,  'DISPONIVEL', NULL, NULL, 'PATIO-SP-01', 'São Paulo'),
  ('GHI3003', 'Honda Civic 2.0',        'EXECUTIVO',    'São Paulo',    129.90,  'DISPONIVEL', NULL, NULL, 'PATIO-SP-02', 'São Paulo'),
  ('JKL4004', 'Toyota Corolla 2.0',     'EXECUTIVO',    'São Paulo',    129.90,  'EM_MANUTENCAO', '2026-01-20 10:00:00', 'Revisão completa', 'PATIO-SP-02', 'São Paulo'),
  ('MNO5005', 'Jeep Compass 2.0',       'INTERMEDIARIO','Rio de Janeiro',159.90, 'DISPONIVEL', NULL, NULL, 'PATIO-RJ-01', 'Rio de Janeiro'),
  ('PQR6006', 'Hyundai Tucson 2.0',     'SUV',          'Rio de Janeiro',249.90, 'DISPONIVEL', NULL, NULL, 'PATIO-RJ-01', 'Rio de Janeiro'),
  ('STU7007', 'BMW X3 2.0',             'PREMIUM',      'São Paulo',    399.90,  'DISPONIVEL', NULL, NULL, 'PATIO-SP-03', 'São Paulo'),
  ('VWX8008', 'Nissan Kicks 1.6',       'INTERMEDIARIO','São Paulo',    159.90,  'DISPONIVEL', NULL, NULL, 'PATIO-SP-01', 'São Paulo')
ON CONFLICT (placa) DO NOTHING;


-- -----------------------------
-- 4) Reservas
-- -----------------------------
-- Status possíveis: ATIVA, CANCELADA, CONCLUIDA, EXPIRADA
INSERT INTO RESERVA (codigo, categoria, cidade_retirada, data_retirada, data_devolucao, valor_estimado, status, cliente_cpf_cnpj) VALUES
  -- Reservas ativas
  ('R100001', 'ECONOMICO',    'São Paulo',     '2026-01-10 10:00:00', '2026-01-12 10:00:00', 179.80, 'ATIVA',     '12345678901'),
  ('R100002', 'EXECUTIVO',    'São Paulo',     '2026-02-01 09:00:00', '2026-02-05 09:00:00', 519.60, 'ATIVA',     '98765432100'),
  ('R100003', 'INTERMEDIARIO','Rio de Janeiro','2026-03-15 08:00:00', '2026-03-20 08:00:00', 799.50, 'ATIVA',     '45678912300'),
  -- Reserva concluída (já virou locação finalizada)
  ('R200001', 'SUV',          'Rio de Janeiro','2026-04-01 10:00:00', '2026-04-07 10:00:00', 1499.40,'CONCLUIDA', '11122233344'),
  -- Reserva cancelada
  ('R300001', 'ECONOMICO',    'São Paulo',     '2026-05-10 10:00:00', '2026-05-12 10:00:00', 179.80, 'CANCELADA', '12345678901'),
  -- Reserva expirada
  ('R400001', 'PREMIUM',      'São Paulo',     '2025-01-01 10:00:00', '2025-01-03 10:00:00', 799.80, 'EXPIRADA',  '55566677788')
ON CONFLICT (codigo) DO NOTHING;


-- -----------------------------
-- 5) Locações
-- -----------------------------
-- StatusLocacao: ATIVA, FINALIZADA
INSERT INTO LOCACAO (
    codigo,
    reserva_codigo,
    veiculo_placa,
    dias_previstos,
    valor_diaria,
    status,
    quilometragem,
    combustivel,
    possui_avarias,
    vistoria_devolucao_km,
    vistoria_devolucao_combustivel,
    vistoria_devolucao_avarias
) VALUES
  -- Locação ativa em São Paulo (econômico)
  ('LOC-ATIVA-01', 'R100001', 'ABC1001', 2,  89.90,  'ATIVA',
    12000, 'CHEIO', FALSE,
    NULL, NULL, NULL),

  -- Locação ativa executiva
  ('LOC-ATIVA-02', 'R100002', 'GHI3003', 4, 129.90,  'ATIVA',
    25000, '3/4',   FALSE,
    NULL, NULL, NULL),

  -- Locação finalizada no Rio (SUV)
  ('LOC-FINAL-01', 'R200001', 'PQR6006', 6, 249.90,  'FINALIZADA',
    18000, 'CHEIO', FALSE,
    19050, 'MEIO',  TRUE),

  -- Locação finalizada intermediário em SP
  ('LOC-FINAL-02', 'R100003', 'VWX8008', 5, 159.90,  'FINALIZADA',
    5000,  '3/4',   FALSE,
    5600,  '1/4',   TRUE)
ON CONFLICT (codigo) DO NOTHING;


-- -----------------------------
-- 6) Auditoria (exemplos)
-- -----------------------------
INSERT INTO auditoria (id, data_hora, operacao, detalhes, usuario) VALUES
  ('AUD-0001', '2025-12-10 10:00:00', 'RESERVA_CRIADA',      'Reserva R100001 criada para João Silva',         'sistema'),
  ('AUD-0002', '2025-12-11 11:30:00', 'LOCACAO_REALIZADA',   'Locação LOC-ATIVA-01 criada a partir da R100001','sistema'),
  ('AUD-0003', '2025-12-12 09:15:00', 'RESERVA_CANCELADA',   'Reserva R300001 cancelada pelo cliente',         'joao.silva'),
  ('AUD-0004', '2025-12-13 16:45:00', 'LOCACAO_FINALIZADA',  'Locação LOC-FINAL-01 finalizada com devolução', 'sistema')
ON CONFLICT (id) DO NOTHING;

