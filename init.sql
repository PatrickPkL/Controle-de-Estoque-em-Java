CREATE DATABASE IF NOT EXISTS sistema_estoque CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_estoque;

CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(30) NOT NULL UNIQUE,
    senha VARCHAR(64) NOT NULL,
    nome_completo VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    quantidade INT NOT NULL DEFAULT 0,
    preco DECIMAL(10,2) NOT NULL,
    id_categoria INT,
    id_usuario_cad INT,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id),
    FOREIGN KEY (id_usuario_cad) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS movimentacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_produto INT NOT NULL,
    tipo ENUM('ENTRADA','SAIDA') NOT NULL,
    quantidade INT NOT NULL,
    data_mov DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_usuario_mov INT,
    FOREIGN KEY (id_produto) REFERENCES produtos(id),
    FOREIGN KEY (id_usuario_mov) REFERENCES usuarios(id)
);

-- Seed data
INSERT IGNORE INTO usuarios (login, senha, nome_completo) VALUES ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Administrador'); -- senha: admin
INSERT IGNORE INTO categorias (nome) VALUES ('Eletrônicos'), ('Escritório'), ('Limpeza');
