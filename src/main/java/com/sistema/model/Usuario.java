package com.sistema.model;

import java.util.Objects;

public class Usuario {
    private Integer id;
    private String login;
    private String senha;
    private String nomeCompleto;

    public Usuario() {}

    public Usuario(Integer id, String login, String senha, String nomeCompleto) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nomeCompleto = nomeCompleto;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    @Override
    public String toString() {
        return nomeCompleto != null ? nomeCompleto : login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
