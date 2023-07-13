package br.com.banco.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "conta")
public class Conta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_conta")
	private Long id;

	@NotNull
	@Size(min = 3, max = 64)
	@NotEmpty(message = "O nome não pode estar vazio, digite o seu nome por gentileza.")
	@Column(name = "nome_responsavel")
	private String nomeResponsavel;

	@Column(name = "data_de_criacao")
	private LocalDateTime dataDeCriacao;

	@OneToMany(mappedBy = "conta")
	private List<Transferencia> transferencias = new ArrayList<>();

	public Conta() {
	}

	public Conta(Long id, String nomeResponsavel, LocalDateTime dataDeCriacao, List<Transferencia> transferencias) {
		this.id = id;
		this.nomeResponsavel = nomeResponsavel;
		this.dataDeCriacao = dataDeCriacao;
		this.transferencias = transferencias;
	}

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public LocalDateTime getDataDeCriacao() {
		return dataDeCriacao;
	}

	public void setDataDeCriacao(LocalDateTime dataDeCriacao) {
		this.dataDeCriacao = dataDeCriacao;
	}

	public List<Transferencia> getTransferencias() {
		return transferencias;
	}

	public void setTransferencias(List<Transferencia> transferencias) {
		this.transferencias = transferencias;
	}
}