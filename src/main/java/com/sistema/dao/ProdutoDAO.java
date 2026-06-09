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

/**
 * DAO (Data Access Object) para a entidade {@link Produto}.
 * <p>
 * Operações CRUD completas: inserir, atualizar, excluir, listar e buscar por nome.
 * As consultas fazem JOIN com as tabelas {@code categorias} e {@code usuarios}
 * para trazer os objetos relacionados.
 */
public class ProdutoDAO {

    /**
     * Insere um novo produto no banco.
     * @param produto Produto a ser inserido.
     * @return {@code true} se uma linha foi inserida.
     * @throws SQLException Se ocorrer erro de banco.
     */
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

    /**
     * Atualiza os dados de um produto existente.
     * @param produto Produto com os novos dados (id deve estar preenchido).
     * @return {@code true} se uma linha foi atualizada.
     * @throws SQLException Se ocorrer erro de banco.
     */
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

    /**
     * Exclui um produto pelo ID.
     * @param id Identificador do produto a ser excluído.
     * @return {@code true} se uma linha foi excluída.
     * @throws SQLException Se ocorrer erro de banco.
     */
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Lista todos os produtos ordenados por nome.
     * <p>
     * Faz JOIN com categorias e usuarios para popular os objetos relacionados.
     * @return Lista de produtos.
     * @throws SQLException Se ocorrer erro de banco.
     */
    public List<Produto> listar() throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nome as cat_nome, u.nome_completo as usu_nome " +
                     "FROM produtos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id " +
                     "LEFT JOIN usuarios u ON p.id_usuario_cad = u.id " +
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

    /**
     * Busca produtos cujo nome contenha o termo informado (LIKE %termo%).
     * @param nome Termo parcial para busca.
     * @return Lista de produtos que correspondem à busca.
     * @throws SQLException Se ocorrer erro de banco.
     */
    public List<Produto> buscarPorNome(String nome) throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.nome as cat_nome, u.nome_completo as usu_nome " +
                     "FROM produtos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id " +
                     "LEFT JOIN usuarios u ON p.id_usuario_cad = u.id " +
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

    /** Mapeia uma linha do ResultSet para um objeto {@link Produto}. */
    private Produto mapResultSetToProduto(ResultSet rs) throws SQLException {
        String catNome = rs.getString("cat_nome");
        Categoria cat = catNome != null ? new Categoria(rs.getInt("id_categoria"), catNome) : null;
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
