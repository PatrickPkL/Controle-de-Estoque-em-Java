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
    FOREIGN KEY (id_produto) REFERENCES produtos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario_mov) REFERENCES usuarios(id)
);

-- ====================================================================
-- MOCK DATA — Dados de demonstração para apresentação prática
-- ====================================================================

-- USUÁRIOS
INSERT IGNORE INTO usuarios (login, senha, nome_completo) VALUES
('admin',    '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Administrador'), -- senha: admin
('operador', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'Operador de Estoque'); -- senha: 1234

-- CATEGORIAS
INSERT IGNORE INTO categorias (nome) VALUES
('Eletrônicos'), ('Escritório'), ('Limpeza'), ('Alimentos'),
('Bebidas'), ('Higiene'), ('Roupas'), ('Ferramentas');

-- PRODUTOS
INSERT IGNORE INTO produtos (id, nome, quantidade, preco, id_categoria, id_usuario_cad) VALUES
-- Eletrônicos (cat 1)
(1,  'Mouse Sem Fio',       50,   89.90,  1, 1),
(2,  'Teclado Mecânico',    30,  249.90,  1, 1),
(3,  'Webcam HD 1080p',     20,  159.50,  1, 1),
(4,  'Hub USB 4 portas',    15,   54.90,  1, 1),
-- Escritório (cat 2)
(5,  'Papel A4 500 folhas', 200,  29.90,  2, 1),
(6,  'Caneta Azul cx c/50', 500,   1.50,  2, 1),
(7,  'Post-it 76x76mm',     200,   8.90,  2, 1),
(8,  'Grampeador Médio',     10,  35.50,  2, 1),
-- Limpeza (cat 3)
(9,  'Detergente Líquido',   80,   4.50,  3, 1),
(10, 'Desinfetante 500ml',   40,   8.90,  3, 1),
(11, 'Álcool 70% 1L',       200,   6.50,  3, 1),
-- Alimentos (cat 4)
(12, 'Café Torrado 500g',    60,  18.90,  4, 1),
(13, 'Arroz Tipo 1 5kg',    100,  25.90,  4, 1),
-- Bebidas (cat 5)
(14, 'Água Mineral 500ml',  200,   2.50,  5, 1),
(15, 'Refrigerante Lata',   150,   7.90,  5, 1),
-- Higiene (cat 6)
(16, 'Álcool Gel 250ml',    100,   9.90,  6, 1),
(17, 'Sabonete Líquido',     80,   3.50,  6, 1),
-- Roupas (cat 7)
(18, 'Camiseta Uniforme',    50,  49.90,  7, 1),
(19, 'Jaleco Branco Tam M',  25,  79.90,  7, 1),
-- Ferramentas (cat 8)
(20, 'Chave Philips',         3,  12.90,  8, 1),  -- Estoque baixo p/ testar validação
(21, 'Multímetro Digital',   10,  89.90,  8, 1);

-- ====================================================================
-- MOVIMENTAÇÕES — Histórico completo de 4 meses (Abril a Julho/2026)
-- ====================================================================

-- ############### ABRIL / 2026 ###############

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Estoque inicial — admin popula o depósito no 1º dia útil
(1,  'ENTRADA', 100, '2026-04-01 08:00:00', 1),
(2,  'ENTRADA',  60, '2026-04-01 08:05:00', 1),
(3,  'ENTRADA',  40, '2026-04-01 08:10:00', 1),
(4,  'ENTRADA',  30, '2026-04-01 08:12:00', 1),
(5,  'ENTRADA', 500, '2026-04-01 08:15:00', 1),
(6,  'ENTRADA', 800, '2026-04-01 08:18:00', 1),
(7,  'ENTRADA', 400, '2026-04-01 08:22:00', 1),
(8,  'ENTRADA',  25, '2026-04-01 08:24:00', 1),
(9,  'ENTRADA', 120, '2026-04-01 08:28:00', 1),
(10, 'ENTRADA',  80, '2026-04-01 08:30:00', 1),
(11, 'ENTRADA', 300, '2026-04-01 08:33:00', 1),
(12, 'ENTRADA',  80, '2026-04-01 08:37:00', 1),
(13, 'ENTRADA', 150, '2026-04-01 08:40:00', 1),
(14, 'ENTRADA', 300, '2026-04-01 08:43:00', 1),
(15, 'ENTRADA', 250, '2026-04-01 08:46:00', 1),
(16, 'ENTRADA', 200, '2026-04-01 08:50:00', 1),
(17, 'ENTRADA', 150, '2026-04-01 08:53:00', 1),
(18, 'ENTRADA',  80, '2026-04-01 08:56:00', 1),
(19, 'ENTRADA',  50, '2026-04-01 09:00:00', 1),
(20, 'ENTRADA',  20, '2026-04-01 09:03:00', 1),
(21, 'ENTRADA',  25, '2026-04-01 09:06:00', 1);

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Saídas de abril — operador começa a usar o sistema
(5,  'SAIDA',   100, '2026-04-03 09:00:00', 2),
(14, 'SAIDA',    30, '2026-04-03 14:00:00', 2),
(9,  'SAIDA',    20, '2026-04-06 08:30:00', 2),
(6,  'SAIDA',   100, '2026-04-07 10:00:00', 2),
(12, 'SAIDA',    10, '2026-04-08 11:00:00', 2),
(1,  'SAIDA',    20, '2026-04-10 09:00:00', 2),
(18, 'SAIDA',    10, '2026-04-13 08:00:00', 2),
(11, 'SAIDA',    30, '2026-04-14 14:30:00', 2),
(7,  'SAIDA',    50, '2026-04-15 10:00:00', 2),
(16, 'SAIDA',    20, '2026-04-16 09:30:00', 2),
(15, 'SAIDA',    40, '2026-04-17 11:00:00', 2),
(13, 'SAIDA',    20, '2026-04-20 08:00:00', 2),
(2,  'SAIDA',    10, '2026-04-22 15:00:00', 2),
(17, 'SAIDA',    20, '2026-04-24 10:30:00', 2),
(5,  'SAIDA',   100, '2026-04-27 09:00:00', 2),
(14, 'SAIDA',    40, '2026-04-29 14:00:00', 2);

-- ############### MAIO / 2026 ###############

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Reposições de maio
(5,  'ENTRADA', 300, '2026-05-04 08:00:00', 1),
(9,  'ENTRADA',  80, '2026-05-04 08:10:00', 1),
(14, 'ENTRADA', 200, '2026-05-04 08:15:00', 1),
(18, 'ENTRADA',  30, '2026-05-04 08:20:00', 1),
(12, 'ENTRADA',  40, '2026-05-11 08:00:00', 1),
(11, 'ENTRADA', 150, '2026-05-18 08:00:00', 1),
-- Saídas de maio
(5,  'SAIDA',   200, '2026-05-05 09:00:00', 2),
(6,  'SAIDA',   150, '2026-05-06 10:00:00', 2),
(7,  'SAIDA',    80, '2026-05-07 11:00:00', 2),
(9,  'SAIDA',    30, '2026-05-08 08:30:00', 2),
(1,  'SAIDA',    15, '2026-05-11 09:00:00', 2),
(2,  'SAIDA',    10, '2026-05-12 14:00:00', 2),
(14, 'SAIDA',    80, '2026-05-13 10:00:00', 2),
(16, 'SAIDA',    30, '2026-05-14 09:30:00', 2),
(17, 'SAIDA',    25, '2026-05-15 11:00:00', 2),
(15, 'SAIDA',    50, '2026-05-18 08:00:00', 2),
(18, 'SAIDA',    20, '2026-05-19 14:00:00', 2),
(11, 'SAIDA',    60, '2026-05-20 10:00:00', 2),
(3,  'SAIDA',    10, '2026-05-21 09:00:00', 2),
(4,  'SAIDA',     5, '2026-05-22 15:00:00', 2),
(13, 'SAIDA',    25, '2026-05-25 08:30:00', 2),
(12, 'SAIDA',    15, '2026-05-26 10:00:00', 2),
(21, 'SAIDA',     5, '2026-05-27 14:00:00', 2),
(8,  'SAIDA',     3, '2026-05-28 09:00:00', 2),
(19, 'SAIDA',     8, '2026-05-29 11:00:00', 1);

-- ############### JUNHO / 2026 ###############

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Entradas iniciais de junho (admin)
(1,  'ENTRADA', 100, '2026-06-01 08:00:00', 1),
(2,  'ENTRADA',  60, '2026-06-01 08:05:00', 1),
(3,  'ENTRADA',  40, '2026-06-01 08:10:00', 1),
(4,  'ENTRADA',  30, '2026-06-01 08:12:00', 1),
(5,  'ENTRADA', 500, '2026-06-01 08:15:00', 1),
(6,  'ENTRADA', 800, '2026-06-01 08:18:00', 1),
(7,  'ENTRADA', 400, '2026-06-01 08:22:00', 1),
(8,  'ENTRADA',  25, '2026-06-01 08:24:00', 1),
(9,  'ENTRADA', 120, '2026-06-01 08:28:00', 1),
(10, 'ENTRADA',  80, '2026-06-01 08:30:00', 1),
(11, 'ENTRADA', 300, '2026-06-01 08:33:00', 1),
(12, 'ENTRADA',  80, '2026-06-01 08:37:00', 1),
(13, 'ENTRADA', 150, '2026-06-01 08:40:00', 1),
(14, 'ENTRADA', 300, '2026-06-01 08:43:00', 1),
(15, 'ENTRADA', 250, '2026-06-01 08:46:00', 1),
(16, 'ENTRADA', 200, '2026-06-01 08:50:00', 1),
(17, 'ENTRADA', 150, '2026-06-01 08:53:00', 1),
(18, 'ENTRADA',  80, '2026-06-01 08:56:00', 1),
(19, 'ENTRADA',  50, '2026-06-01 09:00:00', 1),
(20, 'ENTRADA',  20, '2026-06-01 09:03:00', 1),
(21, 'ENTRADA',  25, '2026-06-01 09:06:00', 1);

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Reposições de junho
(5,  'ENTRADA', 200, '2026-06-10 10:00:00', 1),
(9,  'ENTRADA',  60, '2026-06-12 14:30:00', 1),
(14, 'ENTRADA', 150, '2026-06-15 09:15:00', 2),
(18, 'ENTRADA',  40, '2026-06-18 11:00:00', 2);

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Saídas de junho — 1ª semana
(5,  'SAIDA',   300, '2026-06-03 09:00:00', 2),
(9,  'SAIDA',    40, '2026-06-04 10:30:00', 2),
(14, 'SAIDA',   100, '2026-06-05 11:00:00', 1),
(18, 'SAIDA',    30, '2026-06-06 14:00:00', 2),
(1,  'SAIDA',    50, '2026-06-07 15:00:00', 2),
(2,  'SAIDA',    30, '2026-06-08 09:00:00', 1),
(12, 'SAIDA',    20, '2026-06-08 16:00:00', 2);

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Saídas de junho — 2ª semana
(6,  'SAIDA',   150, '2026-06-10 08:30:00', 2),
(7,  'SAIDA',   100, '2026-06-11 10:00:00', 2),
(11, 'SAIDA',    50, '2026-06-11 14:00:00', 1),
(16, 'SAIDA',    40, '2026-06-12 09:30:00', 2),
(17, 'SAIDA',    30, '2026-06-12 16:00:00', 2),
(15, 'SAIDA',    60, '2026-06-13 11:00:00', 2);

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Saídas de junho — 3ª semana
(4,  'SAIDA',    10, '2026-06-17 08:00:00', 2),
(3,  'SAIDA',    15, '2026-06-17 15:30:00', 1),
(13, 'SAIDA',    30, '2026-06-18 10:00:00', 2),
(8,  'SAIDA',     5, '2026-06-19 09:00:00', 2),
(21, 'SAIDA',     8, '2026-06-19 14:00:00', 2),
(19, 'SAIDA',    10, '2026-06-20 11:00:00', 2);

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Saídas de junho — 4ª semana
(1,  'SAIDA',    20, '2026-06-24 08:30:00', 2),
(10, 'SAIDA',    25, '2026-06-24 15:00:00', 2),
(5,  'SAIDA',   150, '2026-06-25 10:00:00', 2),
(14, 'SAIDA',    60, '2026-06-26 09:00:00', 2),
(12, 'SAIDA',    15, '2026-06-27 14:00:00', 1);

-- ############### JULHO / 2026 ###############

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Reposições de julho
(5,  'ENTRADA', 250, '2026-07-01 08:00:00', 1),
(9,  'ENTRADA',  80, '2026-07-01 08:10:00', 1),
(14, 'ENTRADA', 200, '2026-07-01 08:15:00', 1),
(1,  'ENTRADA',  80, '2026-07-07 08:00:00', 1),
(2,  'ENTRADA',  40, '2026-07-07 08:05:00', 1),
(11, 'ENTRADA', 100, '2026-07-14 08:00:00', 1),
(16, 'ENTRADA', 100, '2026-07-21 08:00:00', 2),
(18, 'ENTRADA',  30, '2026-07-28 08:00:00', 2);

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Saídas de julho — 1ª quinzena
(5,  'SAIDA',   200, '2026-07-02 09:00:00', 2),
(6,  'SAIDA',   100, '2026-07-03 10:00:00', 2),
(7,  'SAIDA',    60, '2026-07-06 08:00:00', 2),
(9,  'SAIDA',    25, '2026-07-07 14:00:00', 2),
(14, 'SAIDA',    70, '2026-07-08 09:30:00', 2),
(1,  'SAIDA',    30, '2026-07-09 11:00:00', 2),
(2,  'SAIDA',    15, '2026-07-10 08:00:00', 2),
(15, 'SAIDA',    40, '2026-07-13 10:00:00', 2),
(16, 'SAIDA',    30, '2026-07-14 14:00:00', 2),
(12, 'SAIDA',    20, '2026-07-15 09:00:00', 2);

INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES
-- Saídas de julho — 2ª quinzena
(17, 'SAIDA',    20, '2026-07-16 10:30:00', 2),
(11, 'SAIDA',    40, '2026-07-17 08:00:00', 2),
(3,  'SAIDA',    10, '2026-07-20 15:00:00', 1),
(18, 'SAIDA',    15, '2026-07-21 09:00:00', 2),
(4,  'SAIDA',     8, '2026-07-22 11:00:00', 2),
(13, 'SAIDA',    20, '2026-07-23 08:30:00', 2),
(8,  'SAIDA',     3, '2026-07-24 10:00:00', 2),
(10, 'SAIDA',    15, '2026-07-27 14:00:00', 2),
(21, 'SAIDA',     5, '2026-07-28 09:00:00', 2),
(19, 'SAIDA',     6, '2026-07-29 11:00:00', 2),
(5,  'SAIDA',   100, '2026-07-30 08:00:00', 2);
