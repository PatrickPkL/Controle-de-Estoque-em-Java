package com.sistema.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Produto {
    private Integer id;
    private String nome;
    private Integer quantidade;
    private BigDecimal preco;
    private Categoria categoria;
    private Usuario usuarioCad;

    public Produto() {}

    public Produto(Integer id, String nome, Integer quantidade, BigDecimal preco, Categoria categoria, Usuario usuarioCad) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.preco = preco;
        this.categoria = categoria;
        this.usuarioCad = usuarioCad;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Usuario getUsuarioCad() { return usuarioCad; }
    public void setUsuarioCad(Usuario usuarioCad) { this.usuarioCad = usuarioCad; }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(id, produto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
