-- ============================================================
-- Migração V1: Criação inicial do schema do AlugaCar
-- ============================================================

-- Tabela de Clientes
CREATE TABLE CLIENTE (
    cpf_cnpj VARCHAR(14) PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    cnh VARCHAR(11) NOT NULL,
    email VARCHAR(150) NOT NULL
);

-- Tabela de Categorias
CREATE TABLE CATEGORIA (
    codigo VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),
    diaria DECIMAL(10, 2) NOT NULL,
    modelos_exemplo VARCHAR(500),
    quantidade_disponivel INT NOT NULL DEFAULT 0
);

-- Tabela de Veículos
CREATE TABLE VEICULO (
    placa VARCHAR(10) PRIMARY KEY,
    modelo VARCHAR(100) NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    diaria DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    manutencao_prevista TIMESTAMP,
    manutencao_nota VARCHAR(500),
    patio_codigo VARCHAR(50),
    patio_localizacao VARCHAR(100),
    CONSTRAINT fk_veiculo_categoria FOREIGN KEY (categoria) 
        REFERENCES CATEGORIA(codigo)
);

-- Tabela de Reservas
CREATE TABLE RESERVA (
    codigo VARCHAR(50) PRIMARY KEY,
    categoria VARCHAR(20) NOT NULL,
    cidade_retirada VARCHAR(100) NOT NULL,
    data_retirada TIMESTAMP NOT NULL,
    data_devolucao TIMESTAMP NOT NULL,
    valor_estimado DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    cliente_cpf_cnpj VARCHAR(14) NOT NULL,
    CONSTRAINT fk_reserva_categoria FOREIGN KEY (categoria) 
        REFERENCES CATEGORIA(codigo),
    CONSTRAINT fk_reserva_cliente FOREIGN KEY (cliente_cpf_cnpj) 
        REFERENCES CLIENTE(cpf_cnpj)
);

-- Tabela de Locações
CREATE TABLE LOCACAO (
    codigo VARCHAR(50) PRIMARY KEY,
    reserva_codigo VARCHAR(50) NOT NULL,
    veiculo_placa VARCHAR(10) NOT NULL,
    dias_previstos INT NOT NULL,
    valor_diaria DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    quilometragem INT,
    combustivel VARCHAR(20),
    possui_avarias BOOLEAN,
    CONSTRAINT fk_locacao_reserva FOREIGN KEY (reserva_codigo) 
        REFERENCES RESERVA(codigo),
    CONSTRAINT fk_locacao_veiculo FOREIGN KEY (veiculo_placa) 
        REFERENCES VEICULO(placa)
);

-- Índices para melhorar performance de consultas
CREATE INDEX idx_veiculo_cidade_categoria_status 
    ON VEICULO(cidade, categoria, status);

CREATE INDEX idx_reserva_cliente 
    ON RESERVA(cliente_cpf_cnpj);

CREATE INDEX idx_reserva_status 
    ON RESERVA(status);

CREATE INDEX idx_locacao_reserva 
    ON LOCACAO(reserva_codigo);

CREATE INDEX idx_locacao_veiculo 
    ON LOCACAO(veiculo_placa);
