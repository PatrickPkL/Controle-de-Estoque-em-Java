package com.sistema.view;

import com.sistema.controller.LoginController;
import javax.swing.SwingUtilities;

public class MainView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Cria a janela de login
            LoginView loginTela = new LoginView();
            
            // 2. Conecta a janela ao controlador (Ativa os botões de entrar e cadastrar)
            new LoginController(loginTela);
            
            // 3. Mostra a tela na tela do usuário
            loginTela.setVisible(true);
        });
    }
}