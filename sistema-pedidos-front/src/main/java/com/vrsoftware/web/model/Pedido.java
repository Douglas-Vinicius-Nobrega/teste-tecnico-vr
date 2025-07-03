package com.vrsoftware.web.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Pedido {
    private UUID id;
    private String produto;
    private Integer quantidade;
    private LocalDateTime dataCriacao;

    public Pedido() {
    }

    public Pedido(UUID id, String produto, Integer quantidade, LocalDateTime dataCriacao) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.dataCriacao = dataCriacao;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedidos = (Pedido) o;
        return Objects.equals(id, pedidos.id) && Objects.equals(produto, pedidos.produto) && Objects.equals(quantidade, pedidos.quantidade) && Objects.equals(dataCriacao, pedidos.dataCriacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, produto, quantidade, dataCriacao);
    }

    @Override
    public String toString() {
        return "Pedidos{" +
                "id=" + id +
                ", produto='" + produto + '\'' +
                ", quantidade=" + quantidade +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}
