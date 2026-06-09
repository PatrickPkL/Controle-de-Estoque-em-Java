package com.sistema.dao;

import com.sistema.model.Usuario;
import com.sistema.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * DAO (Data Access Object) para a entidade {@link Usuario}.
 * <p>
 * Responsável por todas as operações de banco relacionadas a usuários:
 * inserção, busca por login e autenticação (login + senha hash).
 */
public class UsuarioDAO {

    /**
     * Insere um novo usuário na tabela {@code usuarios}.
     * @param usuario O usuário a ser inserido (login, senha hash, nome).
     * @return {@code true} se uma linha foi inserida.
     * @throws SQLException Se ocorrer erro de banco (ex.: login duplicado).
     */
    public boolean inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (login, senha, nome_completo) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getLogin());
            ps.setString(2, usuario.getSenha());
            ps.setString(3, usuario.getNomeCompleto());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Busca um usuário pelo login.
     * @param login Login a ser buscado.
     * @return {@link Optional} contendo o usuário se encontrado, ou vazio.
     * @throws SQLException Se ocorrer erro de banco.
     */
    public Optional<Usuario> buscarPorLogin(String login) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE login = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToUsuario(rs));
            }
        }
        return Optional.empty();
    }

    /**
     * Autentica um usuário comparando login e senha (já em hash).
     * @param login     Login do usuário.
     * @param senhaHash Senha codificada em SHA-256.
     * @return {@link Optional} contendo o usuário se login+senha conferirem.
     * @throws SQLException Se ocorrer erro de banco.
     */
    public Optional<Usuario> autenticar(String login, String senhaHash) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senhaHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToUsuario(rs));
            }
        }
        return Optional.empty();
    }

    /** Mapeia o resultado de uma consulta SQL para um objeto {@link Usuario}. */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("login"),
            rs.getString("senha"),
            rs.getString("nome_completo")
        );
    }
}
