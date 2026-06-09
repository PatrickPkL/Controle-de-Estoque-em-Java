package com.sistema.util;

import com.sistema.model.Usuario;

/**
 * Gerenciador de sessão do usuário logado (singleton).
 * <p>
 * Mantém o {@link Usuario} autenticado durante toda a sessão da aplicação.
 * Utilizado pelos controllers para identificar quem está realizando as operações.
 */
public class SessaoUsuario {
    private static Usuario usuarioLogado;

    /** Define o usuário atual da sessão (chamado após login bem-sucedido). */
    public static void set(Usuario usuario) {
        usuarioLogado = usuario;
    }

    /** Retorna o usuário atualmente logado, ou {@code null} se ninguém estiver logado. */
    public static Usuario get() {
        return usuarioLogado;
    }

    /** Encerra a sessão do usuário (logout). */
    public static void clear() {
        usuarioLogado = null;
    }

    /** Verifica se há um usuário logado no momento. */
    public static boolean isLogado() {
        return usuarioLogado != null;
    }
}
