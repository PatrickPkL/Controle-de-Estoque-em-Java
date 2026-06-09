package com.sistema.controller;

import com.sistema.dao.UsuarioDAO;
import com.sistema.model.Usuario;
import com.sistema.util.HashUtil;
import com.sistema.util.SessaoUsuario;
import com.sistema.view.CadastroUsuarioView;
import com.sistema.view.LoginView;
import com.sistema.view.TelaPrincipalView;
import java.sql.SQLException;
import java.util.Optional;
import javax.swing.JOptionPane;

/**
 * Controlador da tela de login {@link LoginView}.
 * <p>
 * Responsável por autenticar o usuário (validando login + senha hash),
 * navegar para a tela de cadastro de novo usuário e redirecionar
 * ao dashboard principal após login bem-sucedido.
 */
public class LoginController {
    private final LoginView view;
    private final UsuarioDAO usuarioDAO;

    /**
     * Conecta a View ao Controller e ativa os listeners dos botões.
     * @param view Instância da tela de login.
     */
    public LoginController(LoginView view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
        initListeners();
    }

    /** Registra os listeners nos botões Entrar e Criar Conta. */
    private void initListeners() {
        view.getBtnEntrar().addActionListener(e -> autenticar());
        view.getBtnCriarConta().addActionListener(e -> abrirCadastro());
    }

    /**
     * Fluxo de autenticação:
     * <ol>
     *   <li>Valida se login e senha foram preenchidos.</li>
     *   <li>Gera o hash SHA-256 da senha digitada.</li>
     *   <li>Busca no banco um usuário com login + senha hash.</li>
     *   <li>Se encontrado, salva na sessão e abre o dashboard.</li>
     *   <li>Senão, exibe mensagem de erro.</li>
     * </ol>
     */
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

    /** Abre a tela de cadastro de novo usuário e fecha a tela de login. */
    private void abrirCadastro() {
        CadastroUsuarioView cadastroView = new CadastroUsuarioView();
        new CadastroUsuarioController(cadastroView);
        cadastroView.setVisible(true);
        view.dispose();
    }

    /** Abre o dashboard principal (tela de gestão de produtos) e fecha o login. */
    private void abrirDashboard() {
        TelaPrincipalView dash = new TelaPrincipalView();
        new ProdutoController(dash);
        dash.setVisible(true);
        view.dispose();
    }
}