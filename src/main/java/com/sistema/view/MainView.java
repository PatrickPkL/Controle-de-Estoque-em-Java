package com.sistema.view;

import com.sistema.controller.LoginController;
import javax.swing.SwingUtilities;

/**
 * Classe de entrada (main) da aplicação.
 * <p>
 * Inicializa a interface gráfica no Event Dispatch Thread (EDT)
 * do Swing, criando a tela de login e conectando-a ao
 * {@link LoginController}.
 */
public class MainView {

    /**
     * Ponto de entrada da aplicação.
     * <ol>
     *   <li>Cria a tela de login ({@link LoginView}).</li>
     *   <li>Conecta ao controlador {@link LoginController}.</li>
     *   <li>Exibe a janela.</li>
     * </ol>
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView loginTela = new LoginView();
            new LoginController(loginTela);
            loginTela.setVisible(true);
        });
    }
}