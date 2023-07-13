package br.com.banco.entities;

import java.time.LocalDateTime;

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
import javax.validation.constraints.NotNull;

import br.com.banco.enums.Operation;

@Entity
@Table(name = "transferencia")
public class Transferencia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "data_transferencia")
	private LocalDateTime dataTransferencia;

	@Column(name = "valor", nullable = false, columnDefinition = "DECIMAL(20,2)")
	@NotNull
	private Double valor;

	@Enumerated(value = EnumType.STRING)
	@NotNull
	private Operation tipo;


	
	//TODO: Verificar se a prova vai mesmo autorizar o nome do operador ser nulo.
	
	
	
	@Column(name = "nome_operador_transacao")
	private String nomeOperadorTransacao;

	@Column(name = "saldoAtual")
	private Double saldoAtual;

	@ManyToOne
	@JoinColumn(name = "conta_id")
	private Conta conta;
}