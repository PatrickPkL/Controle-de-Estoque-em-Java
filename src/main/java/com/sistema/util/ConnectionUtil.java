package com.sistema.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConnectionUtil {
    private static Connection conn;

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

    private static void initDatabase(Connection conn) {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS categorias (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(50) NOT NULL UNIQUE)");
            st.execute("CREATE TABLE IF NOT EXISTS usuarios (id INT AUTO_INCREMENT PRIMARY KEY, login VARCHAR(30) NOT NULL UNIQUE, senha VARCHAR(64) NOT NULL, nome_completo VARCHAR(100))");
            st.execute("CREATE TABLE IF NOT EXISTS produtos (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100) NOT NULL, quantidade INT NOT NULL DEFAULT 0, preco DECIMAL(10,2) NOT NULL, id_categoria INT, id_usuario_cad INT, FOREIGN KEY (id_categoria) REFERENCES categorias(id), FOREIGN KEY (id_usuario_cad) REFERENCES usuarios(id))");
            st.execute("CREATE TABLE IF NOT EXISTS movimentacoes (id INT AUTO_INCREMENT PRIMARY KEY, id_produto INT NOT NULL, tipo ENUM('ENTRADA','SAIDA') NOT NULL, quantidade INT NOT NULL, data_mov DATETIME DEFAULT CURRENT_TIMESTAMP, id_usuario_mov INT, FOREIGN KEY (id_produto) REFERENCES produtos(id), FOREIGN KEY (id_usuario_mov) REFERENCES usuarios(id))");

            st.execute("INSERT IGNORE INTO usuarios (login, senha, nome_completo) VALUES ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Administrador')");
            st.execute("INSERT IGNORE INTO categorias (nome) VALUES ('Eletrônicos'), ('Escritório'), ('Limpeza')");
        } catch (SQLException e) {
            System.err.println("Erro na inicialização: " + e.getMessage());
        }
    }

    public static synchronized void close() {
        try { 
            if (conn != null && !conn.isClosed()) {
                conn.close(); 
            }
        } catch (Exception ignored) {}
    }
}
