package com.sistema.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Modelo que representa um produto no estoque.
 * <p>
 * Cada produto pertence a uma {@link Categoria} e foi cadastrado
 * por um {@link Usuario}. O estoque é controlado pelo campo {@code quantidade},
 * que é alterado através de {@link Movimentacao movimentações}.
 */
public class Produto {
    private Integer id;
    private String nome;
    private Integer quantidade;
    private BigDecimal preco;
    private Categoria categoria;
    private Usuario usuarioCad;

    /** Construtor padrão. */
    public Produto() {}

    /**
     * Construtor completo.
     * @param id         Identificador único.
     * @param nome       Nome do produto.
     * @param quantidade Quantidade em estoque.
     * @param preco      Preço unitário.
     * @param categoria  Categoria à qual pertence.
     * @param usuarioCad Usuário que cadastrou o produto.
     */
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
