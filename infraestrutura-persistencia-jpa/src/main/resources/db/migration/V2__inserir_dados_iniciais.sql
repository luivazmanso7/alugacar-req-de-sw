-- ============================================================
-- Migração V2: Dados iniciais (seed data)
-- ============================================================

-- Inserir categorias padrão
INSERT INTO CATEGORIA (codigo, nome, descricao, diaria, modelos_exemplo, quantidade_disponivel) VALUES
('ECONOMICO', 'Econômico', 'Veículos compactos e econômicos, ideais para a cidade', 89.90, 'Gol, Onix, HB20', 15),
('EXECUTIVO', 'Executivo', 'Carros de tamanho médio com bom espaço interno', 119.90, 'Civic, Corolla, Cruze', 10),
('INTERMEDIARIO', 'Intermediário', 'Veículos com mais conforto e espaço', 159.90, 'Compass, Kicks, T-Cross', 8),
('SUV', 'SUV', 'Utilitários esportivos com amplo espaço', 249.90, 'Tucson, Tiguan, CR-V', 5),
('PREMIUM', 'Luxo', 'Veículos premium com alto nível de conforto', 399.90, 'BMW X3, Audi Q5, Mercedes GLC', 3);

-- Inserir clientes de exemplo
INSERT INTO CLIENTE (cpf_cnpj, nome, cnh, email) VALUES
('12345678901', 'João Silva', '12345678901', 'joao.silva@email.com'),
('98765432100', 'Maria Santos', '98765432100', 'maria.santos@email.com'),
('45678912300', 'Carlos Oliveira', '45678912300', 'carlos.oliveira@email.com');

-- Inserir veículos disponíveis em São Paulo
INSERT INTO VEICULO (placa, modelo, categoria, cidade, diaria, status, patio_codigo, patio_localizacao) VALUES
('ABC1234', 'Volkswagen Gol 1.0', 'ECONOMICO', 'São Paulo', 89.90, 'DISPONIVEL', 'PATIO-SAO PAULO', 'São Paulo'),
('DEF5678', 'Chevrolet Onix 1.0', 'ECONOMICO', 'São Paulo', 89.90, 'DISPONIVEL', 'PATIO-SAO PAULO', 'São Paulo'),
('GHI9012', 'Hyundai HB20 1.0', 'ECONOMICO', 'São Paulo', 89.90, 'DISPONIVEL', 'PATIO-SAO PAULO', 'São Paulo'),
('JKL3456', 'Honda Civic 2.0', 'EXECUTIVO', 'São Paulo', 119.90, 'DISPONIVEL', 'PATIO-SAO PAULO', 'São Paulo'),
('MNO7890', 'Toyota Corolla 2.0', 'EXECUTIVO', 'São Paulo', 119.90, 'DISPONIVEL', 'PATIO-SAO PAULO', 'São Paulo'),
('PQR1234', 'Jeep Compass 2.0', 'INTERMEDIARIO', 'São Paulo', 159.90, 'DISPONIVEL', 'PATIO-SAO PAULO', 'São Paulo'),
('STU5678', 'Nissan Kicks 1.6', 'INTERMEDIARIO', 'São Paulo', 159.90, 'DISPONIVEL', 'PATIO-SAO PAULO', 'São Paulo'),
('VWX9012', 'Hyundai Tucson 2.0', 'SUV', 'São Paulo', 249.90, 'DISPONIVEL', 'PATIO-SAO PAULO', 'São Paulo'),
('YZA3456', 'BMW X3 2.0', 'PREMIUM', 'São Paulo', 399.90, 'DISPONIVEL', 'PATIO-SAO PAULO', 'São Paulo');

-- Inserir veículos disponíveis no Rio de Janeiro
INSERT INTO VEICULO (placa, modelo, categoria, cidade, diaria, status, patio_codigo, patio_localizacao) VALUES
('BCD2345', 'Volkswagen Gol 1.0', 'ECONOMICO', 'Rio de Janeiro', 89.90, 'DISPONIVEL', 'PATIO-RIO DE JANEIRO', 'Rio de Janeiro'),
('EFG6789', 'Chevrolet Onix 1.0', 'ECONOMICO', 'Rio de Janeiro', 89.90, 'DISPONIVEL', 'PATIO-RIO DE JANEIRO', 'Rio de Janeiro'),
('HIJ0123', 'Honda Civic 2.0', 'EXECUTIVO', 'Rio de Janeiro', 119.90, 'DISPONIVEL', 'PATIO-RIO DE JANEIRO', 'Rio de Janeiro'),
('KLM4567', 'Jeep Compass 2.0', 'INTERMEDIARIO', 'Rio de Janeiro', 159.90, 'DISPONIVEL', 'PATIO-RIO DE JANEIRO', 'Rio de Janeiro'),
('NOP8901', 'Hyundai Tucson 2.0', 'SUV', 'Rio de Janeiro', 249.90, 'DISPONIVEL', 'PATIO-RIO DE JANEIRO', 'Rio de Janeiro');
