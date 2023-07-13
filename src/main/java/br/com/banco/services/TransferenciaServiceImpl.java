package br.com.banco.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.enums.Operation;
import br.com.banco.repositories.TransferenciaRepository;

@Service
public class TransferenciaServiceImpl implements TransferenciaService {
	
    private TransferenciaRepository transferenciaRepository;

    public TransferenciaServiceImpl(TransferenciaRepository transferenciaRepository) {
        this.transferenciaRepository = transferenciaRepository;
    }

    @Override
    public List<Transferencia> getAllTransferencias() {
        return transferenciaRepository.findAll();
    }

//    @Override
//    public List<Transferencia> getTransferenciasPorConta(Long numeroConta) {
//        return transferenciaRepository.findByContaNumeroConta(numeroConta);
//    }
    @Override
    public List<Transferencia> getTransferenciasPorConta(Long id) {
        return transferenciaRepository.findById(id)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }


    @Override
    public List<Transferencia> getTransferenciasPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return transferenciaRepository.findByDataTransferenciaBetween(dataInicio, dataFim);
    }

    @Override
    public List<Transferencia> getTransferenciasPorOperador(String nomeOperador) {
        return transferenciaRepository.findByNomeOperadorTransacao(nomeOperador);
    }

//    @Override
//    public List<Transferencia> getTransferenciasPorPeriodoEOperador(LocalDateTime dataInicio, LocalDateTime dataFim, String nomeOperador) {
//        return transferenciaRepository.findByDataTransferenciaBetweenAndNomeOperadorTransacao(dataInicio, dataFim, nomeOperador);
//    }

//	@Override
//	public List<Transferencia> getTransferenciasPorPeriodoEOperador(LocalDateTime dataInicio, String nomeOperador) {
//		return transferenciaRepository.findByDataTransferenciaAfterAndNomeOperadorTransacao(dataInicio, nomeOperador);
//	}
   
//    @Override
//    public List<Transferencia> getTransferenciasPorPeriodoEOperador(LocalDateTime dataInicio, String nomeOperador) {
//        List<Transferencia> transferencias = transferenciaRepository.findByDataTransferenciaAfterAndNomeOperadorTransacao(dataInicio, nomeOperador);
//
//        // Filtrar as transferências que estão antes da data de início
//        transferencias = transferencias.stream()
//                .filter(transferencia -> transferencia.getDataTransferencia().isAfter(dataInicio))
//                .collect(Collectors.toList());
//
//        return transferencias;
//    }
    
    @Override
    public List<Transferencia> getTransferenciasPorPeriodoEOperador(LocalDateTime dataInicio, String nomeOperador) {
        return transferenciaRepository.findByDataInicioAndNomeOperador(dataInicio, nomeOperador);
    }



    @Override
    public Page<Transferencia> getTransferenciasPaginadas(Pageable pageable) {
        return transferenciaRepository.findAll(pageable);
    }
    
    @Override
    public Transferencia criarTransferencia(Transferencia transferencia) {
        // Defina a data de transferência como a data atual
        transferencia.setDataTransferencia(LocalDateTime.now());
        
        // Calcule o saldo atual da conta após a transferência
        Double saldoAtual = calcularSaldoAtual(transferencia);
        transferencia.setSaldoAtual(saldoAtual);
        
        // Salve a transferência no banco de dados
        return transferenciaRepository.save(transferencia);
    }

    @Override
    public Transferencia atualizarTransferencia(Long id, Transferencia transferencia) {
        // Verifique se a transferência com o ID especificado existe no banco de dados
        Transferencia transferenciaExistente = transferenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transferência não encontrada"));
        
        // Atualize os campos relevantes da transferênciaExistente com os valores da transferencia
        transferenciaExistente.setValor(transferencia.getValor());
        transferenciaExistente.setTipo(transferencia.getTipo());
        transferenciaExistente.setNomeOperadorTransacao(transferencia.getNomeOperadorTransacao());
        
        // Calcule o saldo atual da conta após a atualização
        Double saldoAtual = calcularSaldoAtual(transferenciaExistente);
        transferenciaExistente.setSaldoAtual(saldoAtual);
        
        // Salve a transferência atualizada no banco de dados
        return transferenciaRepository.save(transferenciaExistente);
    }

    private Double calcularSaldoAtual(Transferencia transferencia) {
        // Obtenha o saldo atual da conta associada à transferência
        Conta conta = transferencia.getConta();
        Double saldoAtual = conta.getSaldo();
        
        // Verifique o tipo de operação da transferência
        Operation tipo = transferencia.getTipo();
        Double valor = transferencia.getValor();
        
        // Atualize o saldo atual com base na operação
        switch (tipo) {
            case DEPOSITO:
                saldoAtual += valor;
                break;
            case SAQUE:
                saldoAtual -= valor;
                break;
            case TRANSFERENCIA:
                // Para transferências, o saldo atual não é atualizado neste método,
                // pois o cálculo será feito separadamente para a conta de origem e destino da transferência.
                break;
        }
        
        // Arredonde o saldo atual para duas casas decimais
        saldoAtual = Math.round(saldoAtual * 100.0) / 100.0;
        
        return saldoAtual;
    }

}