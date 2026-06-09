package com.sistema.controller;

import com.sistema.dao.UsuarioDAO;
import com.sistema.model.Usuario;
import com.sistema.util.HashUtil;
import com.sistema.util.SessaoUsuario;
import com.sistema.view.CadastroUsuarioView;
import com.sistema.view.LoginView;
import com.sistema.view.TelaPrincipalView; // Corrigido para usar a sua tela principal real
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Optional;
import javax.swing.JOptionPane;

public class LoginController {
    private final LoginView view;
    private final UsuarioDAO usuarioDAO;

    public LoginController(LoginView view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
        initListeners();
    }

    private void initListeners() {
        view.getBtnEntrar().addActionListener(e -> autenticar());
        view.getBtnCriarConta().addActionListener(e -> abrirCadastro());
    }

    private void autenticar() {
        String login = view.getLogin();
        String senha = view.getSenha();

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Preencha todos os campos!");
            return;
        }

        try {
            String senhaHash = HashUtil.sha256(senha);
            Optional<Usuario> userOpt = usuarioDAO.autenticar(login, senhaHash);

            if (userOpt.isPresent()) {
                SessaoUsuario.set(userOpt.get());
                abrirDashboard();
            } else {
                JOptionPane.showMessageDialog(view, "Usuário ou senha inválidos!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erro ao conectar com o banco: " + ex.getMessage());
        }
    }

    private void abrirCadastro() {
        CadastroUsuarioView cadastroView = new CadastroUsuarioView();
        new CadastroUsuarioController(cadastroView);
        cadastroView.setVisible(true);
        view.dispose();
    }

    private void abrirDashboard() {
        // CORREÇÃO: Alterado de DashboardView para TelaPrincipalView (que é o nome da sua View)
        TelaPrincipalView dash = new TelaPrincipalView();
        new ProdutoController(dash);
        dash.setVisible(true);
        view.dispose();
    }
}