package com.sistema.dao;

import com.sistema.model.Categoria;
import com.sistema.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaDAO {
    
    public List<Categoria> listar() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nome";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id"), rs.getString("nome")));
            }
        }
        return lista;
    }

    public Optional<Categoria> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Categoria(rs.getInt("id"), rs.getString("nome")));
                }
            }
        }
        return Optional.empty();
    }

    public boolean inserir(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nome) VALUES (?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria.getNome());
            return ps.executeUpdate() > 0;
        }
    }
}
