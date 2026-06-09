package com.sistema.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo que representa uma movimentação de entrada ou saída de estoque.
 * <p>
 * Cada movimentação está vinculada a um {@link Produto}, foi registrada
 * por um {@link Usuario} e possui um {@link TipoMovimentacao} que define
 * se a quantidade será somada ou subtraída do estoque.
 */
public class Movimentacao {

    /** Enum que define o tipo da movimentação: ENTRADA (soma) ou SAIDA (subtrai). */
    public enum TipoMovimentacao { ENTRADA, SAIDA }

    private Integer id;
    private Produto produto;
    private TipoMovimentacao tipo;
    private Integer quantidade;
    private LocalDateTime dataMov;
    private Usuario usuarioMov;

    /** Construtor padrão. */
    public Movimentacao() {}

    /**
     * Construtor completo.
     * @param id         Identificador único.
     * @param produto    Produto movimentado.
     * @param tipo       ENTRADA ou SAIDA.
     * @param quantidade Quantidade movimentada.
     * @param dataMov    Data/hora da movimentação.
     * @param usuarioMov Usuário que realizou a movimentação.
     */
    public Movimentacao(Integer id, Produto produto, TipoMovimentacao tipo, Integer quantidade, LocalDateTime dataMov, Usuario usuarioMov) {
        this.id = id;
        this.produto = produto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.dataMov = dataMov;
        this.usuarioMov = usuarioMov;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public LocalDateTime getDataMov() { return dataMov; }
    public void setDataMov(LocalDateTime dataMov) { this.dataMov = dataMov; }

    public Usuario getUsuarioMov() { return usuarioMov; }
    public void setUsuarioMov(Usuario usuarioMov) { this.usuarioMov = usuarioMov; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movimentacao that = (Movimentacao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
