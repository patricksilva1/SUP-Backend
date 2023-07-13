package br.com.banco.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.banco.entities.Transferencia;

@Service
public interface TransferenciaService {
	List<Transferencia> getAllTransferencias();

	List<Transferencia> getTransferenciasPorConta(Long numeroConta);

	List<Transferencia> getTransferenciasPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);

	List<Transferencia> getTransferenciasPorOperador(String nomeOperador);

//	List<Transferencia> getTransferenciasPorPeriodoEOperador(LocalDateTime dataInicio, LocalDateTime dataFim, String nomeOperador);
	List<Transferencia> getTransferenciasPorPeriodoEOperador(LocalDateTime dataInicio, String nomeOperador);
	
	Page<Transferencia> getTransferenciasPaginadas(Pageable pageable);

	Transferencia criarTransferencia(Transferencia transferencia);

	Transferencia atualizarTransferencia(Long id, Transferencia transferencia);

	

}