package com.sistema.dao;

import com.sistema.model.Categoria;
import com.sistema.model.Produto;
import com.sistema.model.Usuario;
import com.sistema.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoDAO {

    public boolean inserir(Produto produto) throws SQLException {
        String sql = "INSERT INTO produtos (nome, quantidade, preco, id_categoria, id_usuario_cad) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, produto.getNome());
            ps.setInt(2, produto.getQuantidade());
            ps.setBigDecimal(3, produto.getPreco());
            ps.setInt(4, produto.getCategoria().getId());
            ps.setInt(5, produto.getUsuarioCad().getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean atualizar(Produto produto) throws SQLException {
        String sql = "UPDATE produtos SET nome = ?, quantidade = ?, preco = ?, id_categoria = ? WHERE id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, produto.getNome());
            ps.setInt(2, produto.getQuantidade());
            ps.setBigDecimal(3, produto.getPreco());
            ps.setInt(4, produto.getCategoria().getId());
            ps.setInt(5, produto.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Produto> listar() throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nome as cat_nome, u.nome_completo as usu_nome " +
                     "FROM produtos p " +
                     "JOIN categorias c ON p.id_categoria = c.id " +
                     "JOIN usuarios u ON p.id_usuario_cad = u.id " +
                     "ORDER BY p.nome";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapResultSetToProduto(rs));
            }
        }
        return lista;
    }

    public List<Produto> buscarPorNome(String nome) throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nome as cat_nome, u.nome_completo as usu_nome " +
                     "FROM produtos p " +
                     "JOIN categorias c ON p.id_categoria = c.id " +
                     "JOIN usuarios u ON p.id_usuario_cad = u.id " +
                     "WHERE p.nome LIKE ? " +
                     "ORDER BY p.nome";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToProduto(rs));
                }
            }
        }
        return lista;
    }

    private Produto mapResultSetToProduto(ResultSet rs) throws SQLException {
        Categoria cat = new Categoria(rs.getInt("id_categoria"), rs.getString("cat_nome"));
        Usuario usu = new Usuario();
        usu.setId(rs.getInt("id_usuario_cad"));
        usu.setNomeCompleto(rs.getString("usu_nome"));
        
        return new Produto(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getInt("quantidade"),
            rs.getBigDecimal("preco"),
            cat,
            usu
        );
    }
}
