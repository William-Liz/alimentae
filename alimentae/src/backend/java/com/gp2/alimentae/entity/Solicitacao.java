
package com.gp2.alimentae.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao")
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantidade_solicitada",
            nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidadeSolicitada;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacao status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ong_id", nullable = false)
    private Ong ong;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doacao_id", nullable = false)
    private Doacao doacao;

    public Solicitacao() {
    }

    @PrePersist
    protected void prePersist() {
        if (dataSolicitacao == null) {
            dataSolicitacao = LocalDateTime.now();
        }

        if (status == null) {
            status = StatusSolicitacao.PENDENTE;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public void setQuantidadeSolicitada(BigDecimal quantidadeSolicitada) {
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public Ong getOng() {
        return ong;
    }

    public void setOng(Ong ong) {
        this.ong = ong;
    }

    public Doacao getDoacao() {
        return doacao;
    }

    public void setDoacao(Doacao doacao) {
        this.doacao = doacao;
    }
}

