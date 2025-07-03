package com.vrsoftware.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class StatusPedido {
    private UUID idPedido;
    private String status;
    private String mensagemErro;
    private LocalDateTime dataProcessamento;

    public StatusPedido() {
    }

    public StatusPedido(UUID idPedido, String status, String mensagemErro, LocalDateTime dataProcessamento) {
        this.idPedido = idPedido;
        this.status = status;
        this.mensagemErro = mensagemErro;
        this.dataProcessamento = dataProcessamento;
    }

    public UUID getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(UUID idPedido) {
        this.idPedido = idPedido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(LocalDateTime dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StatusPedido that = (StatusPedido) o;
        return Objects.equals(idPedido, that.idPedido) && Objects.equals(status, that.status) && Objects.equals(mensagemErro, that.mensagemErro) && Objects.equals(dataProcessamento, that.dataProcessamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido, status, mensagemErro, dataProcessamento);
    }

    @Override
    public String toString() {
        return "StatusPedido{" +
                "idPedido=" + idPedido +
                ", status='" + status + '\'' +
                ", mensagemErro='" + mensagemErro + '\'' +
                ", dataProcessamento=" + dataProcessamento +
                '}';
    }
}
