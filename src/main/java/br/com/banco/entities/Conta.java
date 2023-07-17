package br.com.banco.entities;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    @Column(name = "data_de_criacao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime dataDeCriacao;

    @Column(name = "saldo", nullable = false, columnDefinition = "DECIMAL(20,2)")
    private Double saldo;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transferencia> transferencias = new ArrayList<>();

    public void setDataCriacaoaAdjusted(ZonedDateTime dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }
    
    // Construtores

    public Conta() {
        this.dataDeCriacao = ZonedDateTime.now();
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

    public ZonedDateTime getDataDeCriacao() {
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