package com.sistema.controller;

import com.sistema.dao.UsuarioDAO;
import com.sistema.model.Usuario;
import com.sistema.util.HashUtil;
import com.sistema.view.CadastroUsuarioView;
import com.sistema.view.LoginView;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Controlador da tela de cadastro de novo usuário {@link CadastroUsuarioView}.
 * <p>
 * Responsável por validar os campos, verificar duplicidade de login,
 * gerar o hash da senha e persistir o novo usuário no banco.
 */
public class CadastroUsuarioController {
    private final CadastroUsuarioView view;
    private final UsuarioDAO usuarioDAO;

    /**
     * Conecta a View ao Controller e ativa os listeners dos botões.
     * @param view Instância da tela de cadastro.
     */
    public CadastroUsuarioController(CadastroUsuarioView view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
        initListeners();
    }

    /** Registra os listeners nos botões Salvar e Voltar. */
    private void initListeners() {
        view.getBtnSalvarCadastro().addActionListener(e -> cadastrar());
        view.getBtnVoltar().addActionListener(e -> voltarLogin());
    }

    /**
     * Fluxo de cadastro:
     * <ol>
     *   <li>Valida se login e senha foram preenchidos.</li>
     *   <li>Verifica se o login já está em uso no banco.</li>
     *   <li>Gera hash SHA-256 da senha e insere o novo usuário.</li>
     *   <li>Em caso de sucesso, retorna para a tela de login.</li>
     * </ol>
     */
    private void cadastrar() {
        String login = view.getNovoLogin();
        String senha = view.getNovaSenha();
        String confirmar = view.getConfirmarSenha();

        if (login.isEmpty() || senha.isEmpty() || confirmar.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (senha.length() < 4) {
            JOptionPane.showMessageDialog(view, "Senha deve ter pelo menos 4 caracteres!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!senha.equals(confirmar)) {
            JOptionPane.showMessageDialog(view, "As senhas não coincidem!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (usuarioDAO.buscarPorLogin(login).isPresent()) {
                JOptionPane.showMessageDialog(view, "Este login já está em uso!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario novo = new Usuario(null, login, HashUtil.sha256(senha), login);
            if (usuarioDAO.inserir(novo)) {
                JOptionPane.showMessageDialog(view, "Usuário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                voltarLogin();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Erro ao cadastrar usuário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Retorna à tela de login e fecha a tela de cadastro. */
    private void voltarLogin() {
        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
        view.dispose();
    }
}