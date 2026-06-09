package com.sistema.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Utilitário de conexão com o banco de dados MySQL.
 * <p>
 * Gerencia uma única conexão (singleton) durante todo o ciclo de vida
 * da aplicação. Lê as configurações do arquivo {@code db.properties}
 * localizado em {@code src/main/resources/}.
 * <p>
 * <strong>Auto-initializer:</strong> Na primeira conexão, cria automaticamente
 * as tabelas do banco (se não existirem) e insere dados iniciais
 * (usuário admin e categorias padrão).
 */
public class ConnectionUtil {
    private static Connection conn;

    /**
     * Retorna a conexão ativa (única instância), criando-a se necessário.
     * <p>
     * A primeira chamada carrega o driver JDBC, lê as configurações,
     * estabelece a conexão e executa {@link #initDatabase(Connection)}.
     *
     * @return Conexão JDBC ativa.
     * @throws SQLException Se não conseguir carregar as configurações ou conectar.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            Properties props = new Properties();
            try (InputStream in = ConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (in == null) throw new SQLException("Arquivo db.properties não encontrado.");
                props.load(in);
            } catch (Exception e) {
                throw new SQLException("Erro ao carregar configurações: " + e.getMessage());
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.pass")
                );
                initDatabase(conn);
            } catch (Exception e) {
                throw new SQLException("Erro ao conectar ao banco: " + e.getMessage());
            }
        }
        return conn;
    }

     /**
     * Cria as tabelas do banco (se não existirem) e insere dados iniciais.
     * <p>
     * Tabelas criadas: categorias, usuarios, produtos, movimentacoes.
     * Dados iniciais: admin (admin), operador (1234), 8 categorias,
     * 21 produtos variados e 15 movimentacoes historicas.
     */
    private static void initDatabase(Connection conn) {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS categorias (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(50) NOT NULL UNIQUE)");
            st.execute("CREATE TABLE IF NOT EXISTS usuarios (id INT AUTO_INCREMENT PRIMARY KEY, login VARCHAR(30) NOT NULL UNIQUE, senha VARCHAR(64) NOT NULL, nome_completo VARCHAR(100))");
            st.execute("CREATE TABLE IF NOT EXISTS produtos (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100) NOT NULL, quantidade INT NOT NULL DEFAULT 0, preco DECIMAL(10,2) NOT NULL, id_categoria INT, id_usuario_cad INT, FOREIGN KEY (id_categoria) REFERENCES categorias(id), FOREIGN KEY (id_usuario_cad) REFERENCES usuarios(id))");
            st.execute("CREATE TABLE IF NOT EXISTS movimentacoes (id INT AUTO_INCREMENT PRIMARY KEY, id_produto INT NOT NULL, tipo ENUM('ENTRADA','SAIDA') NOT NULL, quantidade INT NOT NULL, data_mov DATETIME DEFAULT CURRENT_TIMESTAMP, id_usuario_mov INT, FOREIGN KEY (id_produto) REFERENCES produtos(id) ON DELETE CASCADE, FOREIGN KEY (id_usuario_mov) REFERENCES usuarios(id))");

            st.execute("INSERT IGNORE INTO usuarios (login, senha, nome_completo) VALUES ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Administrador')");
            st.execute("INSERT IGNORE INTO usuarios (login, senha, nome_completo) VALUES ('operador', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'Operador de Estoque')");
            st.execute("INSERT IGNORE INTO categorias (nome) VALUES ('Eletrônicos'), ('Escritório'), ('Limpeza'), ('Alimentos'), ('Bebidas'), ('Higiene'), ('Roupas'), ('Ferramentas')");
            st.execute("INSERT IGNORE INTO produtos (id, nome, quantidade, preco, id_categoria, id_usuario_cad) VALUES (1,'Mouse Sem Fio',50,89.90,1,1),(2,'Teclado Mecânico',30,249.90,1,1),(3,'Webcam HD 1080p',20,159.50,1,1),(4,'Hub USB 4 portas',15,54.90,1,1),(5,'Papel A4 500 folhas',200,29.90,2,1),(6,'Caneta Azul cx c/50',500,1.50,2,1),(7,'Post-it 76x76mm',200,8.90,2,1),(8,'Grampeador Médio',10,35.50,2,1),(9,'Detergente Líquido',80,4.50,3,1),(10,'Desinfetante 500ml',40,8.90,3,1),(11,'Álcool 70% 1L',200,6.50,3,1),(12,'Café Torrado 500g',60,18.90,4,1),(13,'Arroz Tipo 1 5kg',100,25.90,4,1),(14,'Água Mineral 500ml',200,2.50,5,1),(15,'Refrigerante Lata',150,7.90,5,1),(16,'Álcool Gel 250ml',100,9.90,6,1),(17,'Sabonete Líquido',80,3.50,6,1),(18,'Camiseta Uniforme',50,49.90,7,1),(19,'Jaleco Branco Tam M',25,79.90,7,1),(20,'Chave Philips',3,12.90,8,1),(21,'Multímetro Digital',10,89.90,8,1)");
            st.execute("INSERT INTO movimentacoes (id_produto, tipo, quantidade, data_mov, id_usuario_mov) VALUES (1,'ENTRADA',100,'2026-06-01 08:00:00',1),(2,'ENTRADA',60,'2026-06-01 08:05:00',1),(3,'ENTRADA',40,'2026-06-01 08:10:00',1),(5,'ENTRADA',500,'2026-06-01 08:15:00',1),(9,'ENTRADA',120,'2026-06-01 08:20:00',1),(12,'ENTRADA',80,'2026-06-01 08:25:00',1),(14,'ENTRADA',300,'2026-06-01 08:30:00',1),(18,'ENTRADA',80,'2026-06-01 08:35:00',1),(5,'SAIDA',300,'2026-06-03 09:00:00',2),(9,'SAIDA',40,'2026-06-04 10:30:00',2),(14,'SAIDA',100,'2026-06-05 11:00:00',2),(18,'SAIDA',30,'2026-06-06 14:00:00',2),(1,'SAIDA',50,'2026-06-07 15:00:00',2),(2,'SAIDA',30,'2026-06-08 09:00:00',1),(12,'SAIDA',20,'2026-06-08 16:00:00',2)");
        } catch (SQLException e) {
            System.err.println("Erro na inicialização: " + e.getMessage());
        }
    }

    /**
     * Fecha a conexão com o banco de dados (se estiver aberta).
     * Chamado ao encerrar a aplicação.
     */
    public static synchronized void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception ignored) {}
    }
}
