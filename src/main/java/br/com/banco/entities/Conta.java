package br.com.banco.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta")
    private Long id;

    @NotBlank(message = "O nome não pode estar vazio. Por favor, digite o seu nome.")
    @Size(min = 3, max = 50)
    @Column(name = "nome_responsavel")
    private String nome;

    @Column(name = "data_de_criacao")
    private LocalDateTime dataDeCriacao;

    @Column(name = "saldo", nullable = false, columnDefinition = "DECIMAL(20,2)")
    private Double saldo;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transferencia> transferencias = new ArrayList<>();

    // Construtores

    public Conta() {
        this.dataDeCriacao = LocalDateTime.now();
        this.saldo = 0.0;
    }

    public Conta(String nome) {
        this();
        this.nome = nome;
    }

    // Getters e Setters

    public Long getId() {
        return id;
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

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public List<Transferencia> getTransferencias() {
        return transferencias;
    }

    // Métodos auxiliares para gerenciamento de transferências

    public void adicionarTransferencia(Transferencia transferencia) {
        transferencias.add(transferencia);
        transferencia.setConta(this);
    }

    public void removerTransferencia(Transferencia transferencia) {
        transferencias.remove(transferencia);
        transferencia.setConta(null);
    }
}