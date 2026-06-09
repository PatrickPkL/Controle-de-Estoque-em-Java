package com.sistema.controller;

import com.sistema.dao.CategoriaDAO;
import com.sistema.dao.MovimentacaoDAO;
import com.sistema.dao.ProdutoDAO;
import com.sistema.model.Categoria;
import com.sistema.model.Movimentacao;
import com.sistema.model.Produto;
import com.sistema.util.SessaoUsuario;
import com.sistema.view.LoginView;
import com.sistema.view.TelaPrincipalView;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador principal do dashboard {@link TelaPrincipalView}.
 * <p>
 * Gerencia o CRUD de produtos, cadastro de categorias e registro
 * de movimentações de entrada/saída. Atualiza as três tabelas
 * (produtos, categorias, movimentações) e o combobox de categorias.
 */
public class ProdutoController {

    private final TelaPrincipalView view;
    private final ProdutoDAO produtoDAO;
    private final CategoriaDAO categoriaDAO;
    private final MovimentacaoDAO movDAO;
    /** Lista de categorias carregada em memória para popular o JComboBox. */
    private List<Categoria> listaCategoriasCarregadas;

    /**
     * Conecta a View ao Controller, inicializa os DAOs, carrega os dados
     * e ativa os listeners.
     * @param view Instância da tela principal.
     */
    public ProdutoController(TelaPrincipalView view) {
        this.view = view;
        this.produtoDAO = new ProdutoDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.movDAO = new MovimentacaoDAO();

        initListeners();
        carregarTabelas();
        carregarCategoriasCombo();
    }

    /** Registra todos os listeners de botões da tela principal. */
    private void initListeners() {
        view.getBtnBuscar().addActionListener(e -> buscarProdutos());
        view.getTxtBuscaField().addActionListener(e -> buscarProdutos());
        view.getBtnCadastrar().addActionListener(e -> salvarProduto(true));
        view.getBtnAtualizar().addActionListener(e -> salvarProduto(false));
        view.getBtnExcluir().addActionListener(e -> excluirProduto());
        view.getBtnCadastrarCategoria().addActionListener(e -> cadastrarCategoria());
        view.getBtnEntrada().addActionListener(e -> registrarMovimentacao(Movimentacao.TipoMovimentacao.ENTRADA));
        view.getBtnSaida().addActionListener(e -> registrarMovimentacao(Movimentacao.TipoMovimentacao.SAIDA));
    }

    /** Recarrega as três tabelas (produtos, categorias, movimentações). */
    private void carregarTabelas() {
        carregarTabelaProdutos(null);
        carregarTabelaCategorias();
        carregarTabelaMovimentacoes();
    }

    /**
     * Popula a tabela de produtos com dados do banco.
     * @param busca Termo de busca (opcional). Se nulo ou vazio, lista todos.
     */
    private void carregarTabelaProdutos(String busca) {
        DefaultTableModel model = (DefaultTableModel) view.getTableProdutos().getModel();
        model.setRowCount(0);

        try {
            List<Produto> produtos = (busca == null || busca.isEmpty())
                    ? produtoDAO.listar()
                    : produtoDAO.buscarPorNome(busca);

            for (Produto p : produtos) {
                model.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    p.getQuantidade(),
                    p.getPreco(),
                    p.getCategoria() != null ? p.getCategoria().getNome() : "Sem Categoria",
                    p.getUsuarioCad() != null ? p.getUsuarioCad().getLogin() : "Sistema"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erro ao carregar produtos: " + ex.getMessage());
        }
    }

    /** Popula a tabela de categorias com todas as categorias do banco. */
    private void carregarTabelaCategorias() {
        DefaultTableModel model = (DefaultTableModel) view.getTableCategorias().getModel();
        model.setRowCount(0);

        try {
            List<Categoria> categorias = categoriaDAO.listar();
            for (Categoria c : categorias) {
                model.addRow(new Object[]{c.getId(), c.getNome()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erro ao carregar categorias: " + ex.getMessage());
        }
    }

    /** Popula a tabela de movimentações com todo o histórico. */
    private void carregarTabelaMovimentacoes() {
        DefaultTableModel model = (DefaultTableModel) view.getTableMovimentacoes().getModel();
        model.setRowCount(0);

        try {
            List<Movimentacao> movimentacoes = movDAO.listar();
            for (Movimentacao m : movimentacoes) {
                model.addRow(new Object[]{
                    m.getId(),
                    m.getProduto() != null ? m.getProduto().getNome() : "Desconhecido",
                    m.getTipo(),
                    m.getQuantidade(),
                    m.getUsuarioMov() != null ? m.getUsuarioMov().getLogin() : "Sistema"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erro ao carregar movimentações: " + ex.getMessage());
        }
    }

    /** Popula o JComboBox de categorias com os nomes do banco. */
    private void carregarCategoriasCombo() {
        try {
            listaCategoriasCarregadas = categoriaDAO.listar();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (Categoria c : listaCategoriasCarregadas) {
                model.addElement(c.getNome());
            }
            view.getCbCategoria().setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erro ao carregar combo de categorias: " + ex.getMessage());
        }
    }

    /** Dispara a busca de produtos pelo termo digitado. */
    private void buscarProdutos() {
        String busca = view.getTxtBusca();
        carregarTabelaProdutos(busca);
    }

    /**
     * Salva um produto (novo ou atualização).
     * @param novo {@code true} para cadastro, {@code false} para atualização.
     */
    private void salvarProduto(boolean novo) {
        try {
            String nome = view.getTxtNome();
            String qtdStr = view.getTxtQuantidade();
            String precoStr = view.getTxtPreco();

            if (nome.isEmpty() || qtdStr.isEmpty() || precoStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int qtd;
            BigDecimal preco;
            try {
                qtd = Integer.parseInt(qtdStr.trim());
                preco = new BigDecimal(precoStr.trim().replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(view, "Quantidade ou Preço inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int index = view.getCbCategoria().getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(view, "Selecione uma categoria!");
                return;
            }
            Categoria cat = listaCategoriasCarregadas.get(index);

            Produto p = new Produto();
            p.setNome(nome);
            p.setQuantidade(qtd);
            p.setPreco(preco);
            p.setCategoria(cat);

            if (novo) {
                p.setUsuarioCad(SessaoUsuario.get());
                if (produtoDAO.inserir(p)) {
                    JOptionPane.showMessageDialog(view, "Produto cadastrado!");
                }
            } else {
                int row = view.getTableProdutos().getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(view, "Selecione um produto na tabela!");
                    return;
                }
                p.setId((int) view.getTableProdutos().getValueAt(row, 0));
                if (produtoDAO.atualizar(p)) {
                    JOptionPane.showMessageDialog(view, "Produto atualizado!");
                }
            }
            carregarTabelaProdutos(null);
            view.limparCamposProduto();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erro ao salvar: " + ex.getMessage());
        }
    }

    /** Exclui o produto selecionado na tabela (com confirmação). */
    private void excluirProduto() {
        int row = view.getTableProdutos().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione um produto para excluir!");
            return;
        }

        int id = (int) view.getTableProdutos().getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(view, "Deseja realmente excluir?", "Confirmação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (produtoDAO.excluir(id)) {
                    JOptionPane.showMessageDialog(view, "Produto excluído!");
                    carregarTabelaProdutos(null);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(view, "Erro ao excluir: " + ex.getMessage());
            }
        }
    }

    /**
     * Registra uma movimentação de entrada ou saída para o produto selecionado.
     * <p>
     * Atualiza automaticamente o estoque e o histórico.
     */
    private void registrarMovimentacao(Movimentacao.TipoMovimentacao tipo) {
        if (!SessaoUsuario.isLogado()) {
            JOptionPane.showMessageDialog(view, "Sessão expirada! Faça login novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            view.dispose();
            javax.swing.SwingUtilities.invokeLater(() -> {
                LoginView login = new LoginView();
                new LoginController(login);
                login.setVisible(true);
            });
            return;
        }

        int row = view.getTableProdutos().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione um produto na tabela de Produtos primeiro!");
            return;
        }

        String qtdStr = view.getTxtQtdMov();
        if (qtdStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Informe a quantidade para movimentar!");
            return;
        }

        try {
            int qtd = Integer.parseInt(qtdStr.trim());
            int idProd = (int) view.getTableProdutos().getValueAt(row, 0);
            int estoqueAtual = (int) view.getTableProdutos().getValueAt(row, 2);

            if (tipo == Movimentacao.TipoMovimentacao.SAIDA && qtd > estoqueAtual) {
                JOptionPane.showMessageDialog(view,
                    "Estoque insuficiente! Disponível: " + estoqueAtual + ", solicitado: " + qtd,
                    "Saldo Insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Produto p = new Produto();
            p.setId(idProd);

            Movimentacao m = new Movimentacao();
            m.setProduto(p);
            m.setTipo(tipo);
            m.setQuantidade(qtd);
            m.setUsuarioMov(SessaoUsuario.get());

            if (movDAO.registrar(m)) {
                JOptionPane.showMessageDialog(view, "Movimentação registrada com sucesso!");
                view.limparCampoMovimentacao();
                carregarTabelas();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Quantidade inválida!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erro ao registrar: " + ex.getMessage());
        }
    }

    /** Cadastra uma nova categoria no banco e atualiza a interface. */
    private void cadastrarCategoria() {
        String nome = view.getTxtNomeCategoria();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Informe o nome da categoria!");
            return;
        }

        try {
            Categoria c = new Categoria(null, nome);
            if (categoriaDAO.inserir(c)) {
                JOptionPane.showMessageDialog(view, "Categoria cadastrada!");
                view.limparCamposCategoria();
                carregarTabelaCategorias();
                carregarCategoriasCombo();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erro ao cadastrar categoria: " + ex.getMessage());
        }
    }
}