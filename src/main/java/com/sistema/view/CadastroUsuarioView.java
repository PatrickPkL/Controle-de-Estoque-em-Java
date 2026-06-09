/*
 * Advanced Cadastro Screen Design
 * Professional UI/UX matching LoginView
 */
package com.sistema.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Tela de Cadastro profissional com design moderno.
 * @author Team
 */
public class CadastroUsuarioView extends javax.swing.JFrame {

    private JPanel panelBackground;
    private JPanel panelCard;
    private JLabel lblTitle;
    private JLabel lblSubtitle;
    private JLabel lblLogin;
    private JLabel lblSenha;
    private JLabel lblConfirmar;
    private JTextField txtNovoLogin;
    private JPasswordField txtNovaSenha;
    private JPasswordField txtConfirmarSenha;
    private JButton btnSalvarCadastro;
    private JButton btnVoltar;
    private JLabel lblIconUser;
    private JLabel lblIconLock;
    private JLabel lblIconCheck;

    public CadastroUsuarioView() {
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(450, 550);
        setResizable(false);

        // BACKGROUND PANEL WITH GRADIENT
        panelBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 102, 153), 0, getHeight(), new Color(0, 51, 102));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelBackground.setLayout(null);
        setContentPane(panelBackground);

        // CARD PANEL (WHITE)
        panelCard = new JPanel();
        panelCard.setLayout(null);
        panelCard.setBounds(50, 50, 350, 420);
        panelCard.setBackground(new Color(255, 255, 255));
        panelBackground.add(panelCard);

        // SHADOW EFFECT
        JPanel shadow = new JPanel();
        shadow.setLayout(null);
        shadow.setBounds(54, 54, 350, 420);
        shadow.setBackground(new Color(0, 0, 0, 30));
        panelBackground.add(shadow);

        // TITLE
        lblTitle = new JLabel("Criar Conta");
        lblTitle.setBounds(50, 30, 250, 40);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(0, 102, 153));
        panelCard.add(lblTitle);

        // SUBTITLE
        lblSubtitle = new JLabel("Preencha os dados para cadastrar");
        lblSubtitle.setBounds(50, 70, 250, 25);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(100, 100, 100));
        panelCard.add(lblSubtitle);

        // LOGIN ICON
        lblIconUser = new JLabel("👤");
        lblIconUser.setBounds(50, 110, 30, 30);
        lblIconUser.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        panelCard.add(lblIconUser);

        // LOGIN LABEL
        lblLogin = new JLabel("Novo Login");
        lblLogin.setBounds(90, 105, 100, 25);
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLogin.setForeground(new Color(60, 60, 60));
        panelCard.add(lblLogin);

        // LOGIN TEXT FIELD
        txtNovoLogin = new JTextField();
        txtNovoLogin.setBounds(50, 135, 250, 40);
        txtNovoLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNovoLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtNovoLogin.setCaretColor(new Color(0, 102, 153));
        panelCard.add(txtNovoLogin);

        // PASSWORD ICON
        lblIconLock = new JLabel("🔒");
        lblIconLock.setBounds(50, 185, 30, 30);
        lblIconLock.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        panelCard.add(lblIconLock);

        // PASSWORD LABEL
        lblSenha = new JLabel("Senha");
        lblSenha.setBounds(90, 180, 100, 25);
        lblSenha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSenha.setForeground(new Color(60, 60, 60));
        panelCard.add(lblSenha);

        // PASSWORD FIELD
        txtNovaSenha = new JPasswordField();
        txtNovaSenha.setBounds(50, 210, 250, 40);
        txtNovaSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNovaSenha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtNovaSenha.setCaretColor(new Color(0, 102, 153));
        panelCard.add(txtNovaSenha);

        // CONFIRM ICON
        lblIconCheck = new JLabel("✓");
        lblIconCheck.setBounds(50, 260, 30, 30);
        lblIconCheck.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblIconCheck.setForeground(new Color(0, 102, 153));
        panelCard.add(lblIconCheck);

        // CONFIRM PASSWORD LABEL
        lblConfirmar = new JLabel("Confirmar Senha");
        lblConfirmar.setBounds(90, 255, 150, 25);
        lblConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblConfirmar.setForeground(new Color(60, 60, 60));
        panelCard.add(lblConfirmar);

        // CONFIRM PASSWORD FIELD
        txtConfirmarSenha = new JPasswordField();
        txtConfirmarSenha.setBounds(50, 285, 250, 40);
        txtConfirmarSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfirmarSenha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtConfirmarSenha.setCaretColor(new Color(0, 102, 153));
        panelCard.add(txtConfirmarSenha);

        // SAVE BUTTON (PRIMARY)
        btnSalvarCadastro = new JButton("Salvar Cadastro");
        btnSalvarCadastro.setBounds(50, 345, 250, 45);
        btnSalvarCadastro.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvarCadastro.setForeground(Color.WHITE);
        btnSalvarCadastro.setBackground(new Color(0, 102, 153));
        btnSalvarCadastro.setBorder(BorderFactory.createEmptyBorder());
        btnSalvarCadastro.setFocusPainted(false);
        btnSalvarCadastro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSalvarCadastro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSalvarCadastro.setBackground(new Color(0, 82, 133));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSalvarCadastro.setBackground(new Color(0, 102, 153));
            }
        });
        panelCard.add(btnSalvarCadastro);

        // BACK BUTTON (SECONDARY)
        btnVoltar = new JButton("Voltar para Login");
        btnVoltar.setBounds(50, 395, 250, 40);
        btnVoltar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVoltar.setForeground(new Color(0, 102, 153));
        btnVoltar.setBackground(Color.WHITE);
        btnVoltar.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 153), 1));
        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVoltar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnVoltar.setBackground(new Color(240, 248, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnVoltar.setBackground(Color.WHITE);
            }
        });
        panelCard.add(btnVoltar);

        // CLOSE BUTTON
        JButton btnClose = new JButton("✕");
        btnClose.setBounds(315, 10, 25, 25);
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnClose.setForeground(Color.WHITE);
        btnClose.setBackground(new Color(0, 0, 0, 0));
        btnClose.setBorder(BorderFactory.createEmptyBorder());
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));
        panelBackground.add(btnClose);
    }

    // Getters para o Controller
    public String getNovoLogin() {
        return txtNovoLogin.getText() != null ? txtNovoLogin.getText().trim() : "";
    }

    public String getNovaSenha() {
        return new String(txtNovaSenha.getPassword());
    }

    public String getConfirmarSenha() {
        return new String(txtConfirmarSenha.getPassword());
    }

    public JButton getBtnSalvarCadastro() {
        return btnSalvarCadastro;
    }

    public JButton getBtnVoltar() {
        return btnVoltar;
    }

    public void limparCampos() {
        txtNovoLogin.setText("");
        txtNovaSenha.setText("");
        txtConfirmarSenha.setText("");
    }

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new CadastroUsuarioView().setVisible(true));
    }
}