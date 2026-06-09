/*
 * Advanced Dashboard Design - TelaPrincipalView
 * Professional UI/UX with modern color scheme and card-based layout
 */
package com.sistema.view;

import com.sistema.util.SessaoUsuario;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Dashboard principal com design moderno e profissional.
 * @author Team
 */
public class TelaPrincipalView extends javax.swing.JFrame {

    // Color Scheme
    private static final Color PRIMARY = new Color(0, 102, 153);
    private static final Color PRIMARY_DARK = new Color(0, 51, 102);
    private static final Color ACCENT = new Color(0, 153, 204);
    private static final Color SUCCESS = new Color(46, 125, 50);
    private static final Color DANGER = new Color(198, 40, 40);
    private static final Color WARNING = new Color(255, 152, 0);
    private static final Color LIGHT_BG = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_DARK = new Color(50, 50, 50);
    private static final Color TEXT_LIGHT = new Color(120, 120, 120);
    private static final Color BORDER = new Color(220, 220, 220);

    // Components
    private JPanel panelHeader;
    private JPanel panelContent;
    private JLabel lblTitle;
    private JLabel lblUserInfo;
    private JButton btnLogout;
    private JTabbedPane tabbedPane;

    // Products Tab
    private JPanel panelProducts;
    private JTextField txtBusca;
    private JButton btnBuscar;
    private JTable tableProdutos;
    private DefaultTableModel modelProdutos;
    private JTextField txtNome;
    private JTextField txtQuantidade;
    private JTextField txtPreco;
    private JComboBox<String> comboCategoria;
    private JButton btnCadastrar;
    private JButton btnAtualizar;
    private JButton btnExcluir;

    // Categories Tab
    private JPanel panelCategorias;
    private JTable tableCategorias;
    private DefaultTableModel modelCategorias;
    private JTextField txtNomeCategoria;
    private JButton btnCadastrarCategoria;

    // Movements Tab
    private JPanel panelMovimentacoes;
    private JTable tableMovimentacoes;
    private DefaultTableModel modelMovimentacoes;
    private JTextField txtQtdMov;
    private JButton btnEntrada;
    private JButton btnSaida;

    public TelaPrincipalView() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setUndecorated(true);
        initComponents();
    }

    private void initComponents() {
        // MAIN CONTAINER
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(LIGHT_BG);
        setContentPane(mainPanel);

        // HEADER
        panelHeader = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, PRIMARY_DARK);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelHeader.setBounds(0, 0, 1000, 70);
        mainPanel.add(panelHeader);

        // TITLE
        lblTitle = new JLabel("Sistema de Controle de Estoque");
        lblTitle.setBounds(20, 20, 400, 30);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(WHITE);
        panelHeader.add(lblTitle);

        // USER INFO
        String usuarioNome = SessaoUsuario.get() != null ? SessaoUsuario.get().getLogin() : "Usuário";
        lblUserInfo = new JLabel("👤 " + usuarioNome);
        lblUserInfo.setBounds(700, 20, 150, 30);
        lblUserInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUserInfo.setForeground(WHITE);
        panelHeader.add(lblUserInfo);

        // LOGOUT BUTTON
        btnLogout = createButton("Sair", DANGER, WHITE);
        btnLogout.setBounds(870, 17, 100, 35);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente sair?", "Confirmar Saída", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                SessaoUsuario.clear();
                dispose();
                new LoginView().setVisible(true);
            }
        });
        panelHeader.add(btnLogout);

        // CLOSE BUTTON (TOP RIGHT)
        JButton btnClose = new JButton("✕");
        btnClose.setBounds(960, 17, 30, 35);
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnClose.setForeground(WHITE);
        btnClose.setBackground(new Color(0, 0, 0, 0));
        btnClose.setBorder(BorderFactory.createEmptyBorder());
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));
        panelHeader.add(btnClose);

        // TABBED PANE
        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(20, 90, 960, 590);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(WHITE);
        mainPanel.add(tabbedPane);

        // ===== PRODUCTS TAB =====
        panelProducts = new JPanel();
        panelProducts.setLayout(null);
        panelProducts.setBackground(WHITE);
        tabbedPane.addTab("📦 Produtos", panelProducts);
        setupProductsTab();

        // ===== CATEGORIES TAB =====
        panelCategorias = new JPanel();
        panelCategorias.setLayout(null);
        panelCategorias.setBackground(WHITE);
        tabbedPane.addTab("🏷️ Categorias", panelCategorias);
        setupCategoriesTab();

        // ===== MOVEMENTS TAB =====
        panelMovimentacoes = new JPanel();
        panelMovimentacoes.setLayout(null);
        panelMovimentacoes.setBackground(WHITE);
        tabbedPane.addTab("🔄 Movimentações", panelMovimentacoes);
        setupMovementsTab();
    }

    private void setupProductsTab() {
        // SEARCH SECTION
        JLabel lblBusca = new JLabel("Buscar por Nome:");
        lblBusca.setBounds(30, 20, 120, 30);
        lblBusca.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelProducts.add(lblBusca);

        txtBusca = new JTextField();
        txtBusca.setBounds(160, 20, 300, 35);
        txtBusca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtBusca.addActionListener(e -> {
            // ENTER triggers search - will be connected by controller
        });
        panelProducts.add(txtBusca);

        btnBuscar = createButton("🔍 Buscar", PRIMARY, WHITE);
        btnBuscar.setBounds(470, 20, 120, 35);
        panelProducts.add(btnBuscar);

        // TABLE
        modelProdutos = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Quantidade", "Preço", "Categoria", "Cadastrado Por"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableProdutos = new JTable(modelProdutos);
        tableProdutos.setRowHeight(28);
        tableProdutos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableProdutos.setSelectionBackground(ACCENT);
        tableProdutos.setGridColor(BORDER);
        tableProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = tableProdutos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(PRIMARY);
        header.setForeground(WHITE);

        JScrollPane scrollProdutos = new JScrollPane(tableProdutos);
        scrollProdutos.setBounds(30, 65, 900, 180);
        scrollProdutos.setBorder(BorderFactory.createLineBorder(BORDER));
        panelProducts.add(scrollProdutos);

        // FORM SECTION
        JLabel lblFormTitle = new JLabel("Dados do Produto");
        lblFormTitle.setBounds(30, 260, 200, 25);
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(PRIMARY);
        panelProducts.add(lblFormTitle);

        // Row 1: Nome
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(30, 295, 80, 25);
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelProducts.add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(120, 292, 250, 32);
        txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNome.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panelProducts.add(txtNome);

        JLabel lblCategoria = new JLabel("Categoria:");
        lblCategoria.setBounds(400, 295, 80, 25);
        lblCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelProducts.add(lblCategoria);

        comboCategoria = new JComboBox<>(new String[]{"Selecione..."});
        comboCategoria.setBounds(485, 292, 200, 32);
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelProducts.add(comboCategoria);

        // Row 2: Quantidade e Preço
        JLabel lblQuantidade = new JLabel("Quantidade:");
        lblQuantidade.setBounds(30, 335, 80, 25);
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelProducts.add(lblQuantidade);

        txtQuantidade = new JTextField();
        txtQuantidade.setBounds(120, 332, 120, 32);
        txtQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtQuantidade.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panelProducts.add(txtQuantidade);

        JLabel lblPreco = new JLabel("Preço:");
        lblPreco.setBounds(260, 335, 50, 25);
        lblPreco.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelProducts.add(lblPreco);

        txtPreco = new JTextField();
        txtPreco.setBounds(320, 332, 150, 32);
        txtPreco.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPreco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panelProducts.add(txtPreco);

        // BUTTONS
        btnCadastrar = createButton("➕ Cadastrar", SUCCESS, WHITE);
        btnCadastrar.setBounds(530, 332, 130, 35);
        panelProducts.add(btnCadastrar);

        btnAtualizar = createButton("✏️ Atualizar", WARNING, WHITE);
        btnAtualizar.setBounds(670, 332, 130, 35);
        panelProducts.add(btnAtualizar);

        btnExcluir = createButton("🗑️ Excluir", DANGER, WHITE);
        btnExcluir.setBounds(810, 332, 120, 35);
        panelProducts.add(btnExcluir);

        // STATS PANEL
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(null);
        statsPanel.setBounds(30, 410, 900, 150);
        statsPanel.setBackground(LIGHT_BG);
        statsPanel.setBorder(BorderFactory.createLineBorder(BORDER));
        panelProducts.add(statsPanel);

        JLabel lblStatsTitle = new JLabel("Estatísticas Rápidas");
        lblStatsTitle.setBounds(20, 15, 200, 25);
        lblStatsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatsTitle.setForeground(PRIMARY);
        statsPanel.add(lblStatsTitle);

        // Stats cards will be populated dynamically
    }

    private void setupCategoriesTab() {
        JLabel lblTitleCat = new JLabel("Gerenciar Categorias");
        lblTitleCat.setBounds(30, 20, 250, 30);
        lblTitleCat.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitleCat.setForeground(PRIMARY);
        panelCategorias.add(lblTitleCat);

        // TABLE
        modelCategorias = new DefaultTableModel(
            new Object[]{"ID", "Nome da Categoria"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableCategorias = new JTable(modelCategorias);
        tableCategorias.setRowHeight(30);
        tableCategorias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableCategorias.setSelectionBackground(ACCENT);
        tableCategorias.setGridColor(BORDER);

        JTableHeader headerCat = tableCategorias.getTableHeader();
        headerCat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerCat.setBackground(PRIMARY);
        headerCat.setForeground(WHITE);

        JScrollPane scrollCat = new JScrollPane(tableCategorias);
        scrollCat.setBounds(30, 60, 500, 350);
        scrollCat.setBorder(BorderFactory.createLineBorder(BORDER));
        panelCategorias.add(scrollCat);

        // NEW CATEGORY FORM
        JPanel formCat = new JPanel();
        formCat.setLayout(null);
        formCat.setBounds(560, 60, 370, 200);
        formCat.setBackground(LIGHT_BG);
        formCat.setBorder(BorderFactory.createLineBorder(BORDER));
        panelCategorias.add(formCat);

        JLabel lblNewCat = new JLabel("Nova Categoria");
        lblNewCat.setBounds(20, 20, 150, 25);
        lblNewCat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNewCat.setForeground(PRIMARY);
        formCat.add(lblNewCat);

        txtNomeCategoria = new JTextField();
        txtNomeCategoria.setBounds(20, 60, 330, 40);
        txtNomeCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNomeCategoria.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formCat.add(txtNomeCategoria);

        btnCadastrarCategoria = createButton("➕ Cadastrar Categoria", SUCCESS, WHITE);
        btnCadastrarCategoria.setBounds(20, 115, 330, 40);
        formCat.add(btnCadastrarCategoria);
    }

    private void setupMovementsTab() {
        JLabel lblTitleMov = new JLabel("Histórico de Movimentações");
        lblTitleMov.setBounds(30, 20, 300, 30);
        lblTitleMov.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitleMov.setForeground(PRIMARY);
        panelMovimentacoes.add(lblTitleMov);

        // TABLE
        modelMovimentacoes = new DefaultTableModel(
            new Object[]{"ID", "Produto", "Tipo", "Quantidade", "Data", "Usuário"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableMovimentacoes = new JTable(modelMovimentacoes);
        tableMovimentacoes.setRowHeight(28);
        tableMovimentacoes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableMovimentacoes.setSelectionBackground(ACCENT);
        tableMovimentacoes.setGridColor(BORDER);

        JTableHeader headerMov = tableMovimentacoes.getTableHeader();
        headerMov.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerMov.setBackground(PRIMARY);
        headerMov.setForeground(WHITE);

        JScrollPane scrollMov = new JScrollPane(tableMovimentacoes);
        scrollMov.setBounds(30, 60, 900, 280);
        scrollMov.setBorder(BorderFactory.createLineBorder(BORDER));
        panelMovimentacoes.add(scrollMov);

        // REGISTRATION PANEL
        JPanel regPanel = new JPanel();
        regPanel.setLayout(null);
        regPanel.setBounds(30, 360, 900, 200);
        regPanel.setBackground(LIGHT_BG);
        regPanel.setBorder(BorderFactory.createLineBorder(BORDER));
        panelMovimentacoes.add(regPanel);

        JLabel lblRegTitle = new JLabel("Registrar Movimentação");
        lblRegTitle.setBounds(20, 15, 250, 25);
        lblRegTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRegTitle.setForeground(PRIMARY);
        regPanel.add(lblRegTitle);

        JLabel lblInst = new JLabel("Selecione um produto na aba Produtos primeiro");
        lblInst.setBounds(20, 45, 350, 20);
        lblInst.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInst.setForeground(TEXT_LIGHT);
        regPanel.add(lblInst);

        JLabel lblQtd = new JLabel("Quantidade:");
        lblQtd.setBounds(20, 80, 90, 30);
        lblQtd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        regPanel.add(lblQtd);

        txtQtdMov = new JTextField();
        txtQtdMov.setBounds(120, 80, 150, 35);
        txtQtdMov.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtQtdMov.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        regPanel.add(txtQtdMov);

        btnEntrada = createButton("📥 Registrar Entrada", SUCCESS, WHITE);
        btnEntrada.setBounds(300, 75, 200, 45);
        regPanel.add(btnEntrada);

        btnSaida = createButton("📤 Registrar Saída", DANGER, WHITE);
        btnSaida.setBounds(520, 75, 200, 45);
        regPanel.add(btnSaida);
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            private final Color hoverBg = bg.darker();
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    // ===== GETTERS FOR CONTROLLER =====

    public String getTxtBusca() { return txtBusca.getText(); }
    public JTextField getTxtBuscaField() { return txtBusca; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JTable getTableProdutos() { return tableProdutos; }
    public String getTxtNome() { return txtNome.getText(); }
    public String getTxtQuantidade() { return txtQuantidade.getText(); }
    public String getTxtPreco() { return txtPreco.getText(); }
    public JComboBox<String> getCbCategoria() { return comboCategoria; }
    public String getCategoriaSelecionada() {
        return comboCategoria.getSelectedItem() != null ? comboCategoria.getSelectedItem().toString() : "";
    }
    public JButton getBtnCadastrar() { return btnCadastrar; }
    public JButton getBtnAtualizar() { return btnAtualizar; }
    public JButton getBtnExcluir() { return btnExcluir; }
    public void limparCamposProduto() {
        txtNome.setText("");
        txtQuantidade.setText("");
        txtPreco.setText("");
    }

    public JTable getTableCategorias() { return tableCategorias; }
    public String getTxtNomeCategoria() { return txtNomeCategoria.getText(); }
    public JButton getBtnCadastrarCategoria() { return btnCadastrarCategoria; }
    public void limparCamposCategoria() { txtNomeCategoria.setText(""); }

    public JTable getTableMovimentacoes() { return tableMovimentacoes; }
    public JButton getBtnEntrada() { return btnEntrada; }
    public JButton getBtnSaida() { return btnSaida; }
    public String getTxtQtdMov() { return txtQtdMov.getText(); }
    public void limparCampoMovimentacao() { txtQtdMov.setText(""); }

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new TelaPrincipalView().setVisible(true));
    }
}