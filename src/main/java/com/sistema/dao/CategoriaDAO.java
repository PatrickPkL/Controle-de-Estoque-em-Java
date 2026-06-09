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

/**
 * DAO (Data Access Object) para a entidade {@link Categoria}.
 * <p>
 * Operações: listar todas as categorias, buscar por ID e inserir nova categoria.
 */
public class CategoriaDAO {

    /**
     * Lista todas as categorias ordenadas por nome.
     * @return Lista de categorias.
     * @throws SQLException Se ocorrer erro de banco.
     */
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

    /**
     * Busca uma categoria pelo ID.
     * @param id Identificador da categoria.
     * @return {@link Optional} contendo a categoria se encontrada.
     * @throws SQLException Se ocorrer erro de banco.
     */
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

    /**
     * Insere uma nova categoria.
     * @param categoria Categoria a ser inserida (nome deve ser único).
     * @return {@code true} se uma linha foi inserida.
     * @throws SQLException Se ocorrer erro de banco (ex.: nome duplicado).
     */
    public boolean inserir(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nome) VALUES (?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria.getNome());
            return ps.executeUpdate() > 0;
        }
    }
}
