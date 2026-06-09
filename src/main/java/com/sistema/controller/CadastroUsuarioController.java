package com.sistema.controller;

import com.sistema.dao.UsuarioDAO;
import com.sistema.model.Usuario;
import com.sistema.util.HashUtil;
import com.sistema.view.CadastroUsuarioView;
import com.sistema.view.LoginView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class CadastroUsuarioController {
    private final CadastroUsuarioView view;
    private final UsuarioDAO usuarioDAO;

    public CadastroUsuarioController(CadastroUsuarioView view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
        initListeners();
    }

    private void initListeners() {
        view.getBtnSalvarCadastro().addActionListener(e -> cadastrar());
        view.getBtnVoltar().addActionListener(e -> voltarLogin());
    }

    private void cadastrar() {
        String login = view.getNovoLogin();
        String senha = view.getNovaSenha();

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Preencha todos os campos!");
            return;
        }

        try {
            if (usuarioDAO.buscarPorLogin(login).isPresent()) {
                JOptionPane.showMessageDialog(view, "Este login já está em uso!");
                return;
            }

            Usuario novo = new Usuario(null, login, HashUtil.sha256(senha), login); 
            if (usuarioDAO.inserir(novo)) {
                JOptionPane.showMessageDialog(view, "Usuário cadastrado com sucesso!");
                voltarLogin();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erro ao cadastrar usuário: " + ex.getMessage());
        }
    }

    private void voltarLogin() {
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
        view.dispose();
    }
}