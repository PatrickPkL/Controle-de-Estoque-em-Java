/*
 * Advanced Login Screen Design
 * Professional UI/UX with modern color scheme
 */
package com.sistema.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Tela de Login profissional com design moderno.
 * @author Team
 */
public class LoginView extends javax.swing.JFrame {

    private JPanel panelBackground;
    private JPanel panelCard;
    private JLabel lblTitle;
    private JLabel lblSubtitle;
    private JLabel lblUser;
    private JLabel lblPassword;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnCadastrarse;
    private JLabel lblIconUser;
    private JLabel lblIconLock;

    public LoginView() {
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
        panelCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panelBackground.add(panelCard);

        // SHADOW EFFECT (simulated with multiple panels)
        JPanel shadow = new JPanel();
        shadow.setLayout(null);
        shadow.setBounds(54, 54, 350, 420);
        shadow.setBackground(new Color(0, 0, 0, 30));
        panelBackground.add(shadow);

        // TITLE
        lblTitle = new JLabel("Sistema de Estoque");
        lblTitle.setBounds(50, 30, 250, 40);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(0, 102, 153));
        panelCard.add(lblTitle);

        // SUBTITLE
        lblSubtitle = new JLabel("Faça login para continuar");
        lblSubtitle.setBounds(50, 70, 250, 25);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(100, 100, 100));
        panelCard.add(lblSubtitle);

        // USER ICON LABEL
        lblIconUser = new JLabel("👤");
        lblIconUser.setBounds(50, 120, 30, 30);
        lblIconUser.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        panelCard.add(lblIconUser);

        // USER LABEL
        lblUser = new JLabel("Usuário");
        lblUser.setBounds(90, 115, 100, 25);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(60, 60, 60));
        panelCard.add(lblUser);

        // USER TEXT FIELD
        txtLogin = new JTextField();
        txtLogin.setBounds(50, 145, 250, 40);
        txtLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtLogin.setCaretColor(new Color(0, 102, 153));
        panelCard.add(txtLogin);

        // LOCK ICON LABEL
        lblIconLock = new JLabel("🔒");
        lblIconLock.setBounds(50, 200, 30, 30);
        lblIconLock.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        panelCard.add(lblIconLock);

        // PASSWORD LABEL
        lblPassword = new JLabel("Senha");
        lblPassword.setBounds(90, 195, 100, 25);
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassword.setForeground(new Color(60, 60, 60));
        panelCard.add(lblPassword);

        // PASSWORD FIELD
        txtSenha = new JPasswordField();
        txtSenha.setBounds(50, 225, 250, 40);
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtSenha.setCaretColor(new Color(0, 102, 153));
        panelCard.add(txtSenha);

        // ENTER BUTTON (PRIMARY)
        btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(50, 290, 250, 45);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setBackground(new Color(0, 102, 153));
        btnEntrar.setBorder(BorderFactory.createEmptyBorder());
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEntrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnEntrar.setBackground(new Color(0, 82, 133));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnEntrar.setBackground(new Color(0, 102, 153));
            }
        });
        panelCard.add(btnEntrar);

        // REGISTER BUTTON (SECONDARY)
        btnCadastrarse = new JButton("Cadastrar-se");
        btnCadastrarse.setBounds(50, 345, 250, 40);
        btnCadastrarse.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCadastrarse.setForeground(new Color(0, 102, 153));
        btnCadastrarse.setBackground(Color.WHITE);
        btnCadastrarse.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 153), 1));
        btnCadastrarse.setFocusPainted(false);
        btnCadastrarse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCadastrarse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCadastrarse.setBackground(new Color(240, 248, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCadastrarse.setBackground(Color.WHITE);
            }
        });
        panelCard.add(btnCadastrarse);

        // CLOSE BUTTON (TOP RIGHT)
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
    public String getLogin() {
        return txtLogin.getText() != null ? txtLogin.getText().trim() : "";
    }

    public String getSenha() {
        return new String(txtSenha.getPassword());
    }

    public JButton getBtnEntrar() {
        return btnEntrar;
    }

    public JButton getBtnCriarConta() {
        return btnCadastrarse;
    }

    public void limparCampos() {
        txtLogin.setText("");
        txtSenha.setText("");
    }

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {}
        java.awt.EventQueue.invokeLater(() -> new LoginView().setVisible(true));
    }
}