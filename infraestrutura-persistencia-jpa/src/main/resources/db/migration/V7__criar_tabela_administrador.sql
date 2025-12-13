CREATE TABLE ADMINISTRADOR (
    id VARCHAR(50) PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    email VARCHAR(150) NOT NULL,
    login VARCHAR(30) NOT NULL UNIQUE,
    senha_hash VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO'
);

CREATE INDEX idx_administrador_login ON ADMINISTRADOR(login);

INSERT INTO ADMINISTRADOR (id, nome, email, login, senha_hash, status) VALUES
('admin-001', 'Administrador Principal', 'admin@alugacar.com', 'admin', 'HASH_123456', 'ATIVO');

