package com.sistema.dao;

import com.sistema.model.Movimentacao;
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
 * DAO (Data Access Object) para a entidade {@link Movimentacao}.
 * <p>
 * Responsável por registrar movimentações de entrada/saída e listar o histórico.
 * <strong>Regra de negócio:</strong> Ao registrar uma movimentação, o estoque
 * do produto é atualizado automaticamente na mesma transação (commit/rollback).
 */
public class MovimentacaoDAO {

    /**
     * Registra uma movimentação e atualiza o estoque do produto em uma única transação.
     * <p>
     * Se o tipo for ENTRADA, soma a quantidade ao estoque; se SAIDA, subtrai.
     * Ambas as operações são executadas dentro de uma transação: se falhar,
     * um rollback é executado para manter a consistência dos dados.
     *
     * @param mov Movimentação a ser registrada (produto, tipo, quantidade, usuário).
     * @return {@code true} se a operação foi concluída.
     * @throws SQLException Se ocorrer erro de banco (a transação é revertida).
     */
    public boolean registrar(Movimentacao mov) throws SQLException {
        String sqlMov = "INSERT INTO movimentacoes (id_produto, tipo, quantidade, id_usuario_mov) VALUES (?, ?, ?, ?)";
        String sqlEstoque = mov.getTipo() == Movimentacao.TipoMovimentacao.ENTRADA
            ? "UPDATE produtos SET quantidade = quantidade + ? WHERE id = ?"
            : "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ?";

        if (mov.getTipo() == Movimentacao.TipoMovimentacao.SAIDA) {
            String checkSql = "SELECT quantidade FROM produtos WHERE id = ?";
            try (Connection checkConn = ConnectionUtil.getConnection();
                 PreparedStatement checkPs = checkConn.prepareStatement(checkSql)) {
                checkPs.setInt(1, mov.getProduto().getId());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        int estoqueAtual = rs.getInt("quantidade");
                        if (estoqueAtual < mov.getQuantidade()) {
                            throw new SQLException("Estoque insuficiente! Disponível: " + estoqueAtual);
                        }
                    } else {
                        throw new SQLException("Produto não encontrado.");
                    }
                }
            }
        }

        Connection conn = ConnectionUtil.getConnection();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlMov)) {
                ps.setInt(1, mov.getProduto().getId());
                ps.setString(2, mov.getTipo().name());
                ps.setInt(3, mov.getQuantidade());
                ps.setInt(4, mov.getUsuarioMov().getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlEstoque)) {
                ps.setInt(1, mov.getQuantidade());
                ps.setInt(2, mov.getProduto().getId());
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Lista o histórico de movimentações de um produto específico.
     * @param idProduto ID do produto.
     * @return Lista de movimentações ordenadas da mais recente para a mais antiga.
     * @throws SQLException Se ocorrer erro de banco.
     */
    public List<Movimentacao> listarPorProduto(int idProduto) throws SQLException {
        List<Movimentacao> lista = new ArrayList<>();
        String sql = "SELECT m.*, u.nome_completo as usu_nome, u.login as usu_login " +
                     "FROM movimentacoes m " +
                     "LEFT JOIN usuarios u ON m.id_usuario_mov = u.id " +
                     "WHERE m.id_produto = ? " +
                     "ORDER BY m.data_mov DESC";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProduto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToMovimentacao(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Lista todas as movimentações do sistema (histórico geral).
     * <p>
     * Usa LEFT JOIN para garantir que o histórico apareça mesmo se
     * o produto ou usuário associado forem removidos.
     * @return Lista de movimentações ordenadas da mais recente para a mais antiga.
     * @throws SQLException Se ocorrer erro de banco.
     */
    public List<Movimentacao> listar() throws SQLException {
        List<Movimentacao> lista = new ArrayList<>();
        String sql = "SELECT m.*, u.nome_completo as usu_nome, u.login as usu_login, p.nome as prod_nome " +
                     "FROM movimentacoes m " +
                     "LEFT JOIN usuarios u ON m.id_usuario_mov = u.id " +
                     "LEFT JOIN produtos p ON m.id_produto = p.id " +
                     "ORDER BY m.data_mov DESC";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Movimentacao mov = mapResultSetToMovimentacao(rs);
                if (mov.getProduto() != null) {
                    mov.getProduto().setNome(rs.getString("prod_nome"));
                }
                lista.add(mov);
            }
        }
        return lista;
    }

    /** Mapeia uma linha do ResultSet para um objeto {@link Movimentacao}. */
    private Movimentacao mapResultSetToMovimentacao(ResultSet rs) throws SQLException {
        Usuario usu = new Usuario();
        usu.setId(rs.getInt("id_usuario_mov"));
        usu.setNomeCompleto(rs.getString("usu_nome"));
        usu.setLogin(rs.getString("usu_login"));

        Produto prod = new Produto();
        prod.setId(rs.getInt("id_produto"));

        Movimentacao mov = new Movimentacao();
        mov.setId(rs.getInt("id"));
        mov.setProduto(prod);
        mov.setTipo(Movimentacao.TipoMovimentacao.valueOf(rs.getString("tipo")));
        mov.setQuantidade(rs.getInt("quantidade"));

        if (rs.getTimestamp("data_mov") != null) {
            mov.setDataMov(rs.getTimestamp("data_mov").toLocalDateTime());
        }

        mov.setUsuarioMov(usu);
        return mov;
    }
}