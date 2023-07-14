package br.com.banco.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;

@Service
@EnableJpaRepositories
public interface TransferenciaService {
	List<Transferencia> getAllTransferencias();

	List<Transferencia> getTransferenciasPorConta(Long numeroConta);

	List<Transferencia> getTransferenciasPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);

	List<Transferencia> getTransferenciasPorOperador(String nomeOperador);

	List<Transferencia> getTransferenciasPorPeriodoEOperador(LocalDateTime dataInicio, String nomeOperador);
	
	Page<Transferencia> getTransferenciasPaginadas(Pageable pageable);

	Transferencia criarTransferencia(Transferencia transferencia);

	Transferencia atualizarTransferencia(Long id, Transferencia transferencia);
	
	public void sacar(Long idConta, double valor);
	
	public Conta obterContaPorId(Long id);
}