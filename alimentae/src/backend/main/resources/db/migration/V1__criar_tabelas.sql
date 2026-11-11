CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL,
    CONSTRAINT chk_usuario_tipo CHECK (tipo_usuario IN ('EMPRESA', 'ONG'))
);

CREATE TABLE empresa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_fantasia VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    endereco VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    usuario_id BIGINT NOT NULL UNIQUE,

    CONSTRAINT fk_empresa_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE ong (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    endereco VARCHAR(255) NOT NULL,
    area_atuacao VARCHAR(150) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    usuario_id BIGINT NOT NULL UNIQUE,

    CONSTRAINT fk_ong_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE doacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_alimento VARCHAR(120) NOT NULL,
    descricao TEXT,
    quantidade DECIMAL(10,2) NOT NULL,
    quantidade_disponivel DECIMAL(10,2) NOT NULL,
    unidade_medida VARCHAR(20) NOT NULL,
    validade DATE NOT NULL,
    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    empresa_id BIGINT NOT NULL,

    CONSTRAINT fk_doacao_empresa FOREIGN KEY (empresa_id) REFERENCES empresa(id),

    CONSTRAINT chk_doacao_quantidade
    CHECK (quantidade > 0 AND quantidade_disponivel >= 0
    AND quantidade_disponivel <= quantidade),

    CONSTRAINT chk_doacao_unidade
        CHECK (unidade_medida IN ('KG', 'GRAMA', 'LITRO', 'MILILITRO', 'UNIDADE', 'PACOTE', 'CAIXA')),

    CONSTRAINT chk_doacao_status
        CHECK (status IN ('DISPONIVEL', 'ESGOTADA', 'CANCELADA'))
);

CREATE TABLE solicitacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantidade_solicitada DECIMAL(10,2) NOT NULL,
    data_solicitacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    ong_id BIGINT NOT NULL,
    doacao_id BIGINT NOT NULL,

    CONSTRAINT fk_solicitacao_ong FOREIGN KEY (ong_id) REFERENCES ong(id),

    CONSTRAINT fk_solicitacao_doacao FOREIGN KEY (doacao_id) REFERENCES doacao(id),

     CONSTRAINT chk_solicitacao_quantidade
         CHECK (quantidade_solicitada > 0),

    CONSTRAINT chk_solicitacao_status
         CHECK (status IN ('PENDENTE', 'APROVADA', 'RECUSADA', 'CANCELADA', 'CONCLUIDA'))
);