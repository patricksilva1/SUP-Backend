package br.com.banco.dtos;

import java.time.LocalDateTime;

public class ContaDto {

    private Long id;
    private String nome;
    private LocalDateTime dataDeCriacao;
    private Double saldo;

    // Construtores

    public ContaDto() {
    }

    public ContaDto(Long id, String nome, LocalDateTime dataDeCriacao, Double saldo) {
        this.id = id;
        this.nome = nome;
        this.dataDeCriacao = dataDeCriacao;
        this.saldo = saldo;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDataDeCriacao() {
        return dataDeCriacao;
    }

    public void setDataDeCriacao(LocalDateTime dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }
}