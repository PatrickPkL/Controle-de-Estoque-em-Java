package com.sistema.util;

import com.sistema.model.Usuario;

public class SessaoUsuario {
    private static Usuario usuarioLogado;

    public static void set(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static Usuario get() {
        return usuarioLogado;
    }

    public static void clear() {
        usuarioLogado = null;
    }

    public static boolean isLogado() {
        return usuarioLogado != null;
    }
}
