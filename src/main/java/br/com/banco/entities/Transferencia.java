package br.com.banco.entities;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.banco.enums.Operation;

@Entity
@Table(name = "transferencia")
public class Transferencia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

//	@Column(name = "data_transferencia")
//	private LocalDateTime dataTransferencia;
	@Column(name = "data_transferencia", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private ZonedDateTime dataTransferencia;

	@Column(name = "valor", nullable = false, columnDefinition = "DECIMAL(20,2)")
	@NotNull
	private Double valor;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	private Operation tipo;

	// TODO: Verificar se a prova vai mesmo autorizar o nome do operador ser nulo.

	@Column(name = "nome_operador_transacao")
	private String nomeOperadorTransacao;

	@Column(name = "saldoAtual")
	private Double saldoAtual;

	@ManyToOne
	@JoinColumn(name = "conta_id")
	@JsonIgnore // Para evitar a serialização recursiva
	private Conta conta;

	@Transient
	private Conta contaDestino;
	
	public void setDataTransferenciaAdjusted(ZonedDateTime dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }
	
	// Getters and Setters

	public Conta getContaDestino() {
		return contaDestino;
	}

	public void setContaDestino(Conta contaDestino) {
		this.contaDestino = contaDestino;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
//	public LocalDateTime getDataTransferencia() {
//		return dataTransferencia;
//	}

	public ZonedDateTime getDataTransferencia() {
		return dataTransferencia;
	}

//	public void setDataTransferencia(LocalDateTime dataTransferencia) {
//		this.dataTransferencia = dataTransferencia;
//	}
	public void setDataTransferencia(ZonedDateTime dataTransferencia) {
		this.dataTransferencia = dataTransferencia;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Operation getTipo() {
		return tipo;
	}

	public void setTipo(Operation tipo) {
		this.tipo = tipo;
	}

	public String getNomeOperadorTransacao() {
		return nomeOperadorTransacao;
	}

	public void setNomeOperadorTransacao(String nomeOperadorTransacao) {
		this.nomeOperadorTransacao = nomeOperadorTransacao;
	}

	public Double getSaldoAtual() {
		return saldoAtual;
	}

	public void setSaldoAtual(Double saldoAtual) {
		this.saldoAtual = saldoAtual;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}
}