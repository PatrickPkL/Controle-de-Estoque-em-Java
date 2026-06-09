package com.sistema.dao;

import com.sistema.model.Usuario;
import com.sistema.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {
    
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

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("login"),
            rs.getString("senha"),
            rs.getString("nome_completo")
        );
    }
}
