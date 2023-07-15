package br.com.banco.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.enums.Operation;
import br.com.banco.repositories.ContaRepository;
import br.com.banco.repositories.TransferenciaRepository;

@Service
public class TransferenciaServiceImpl implements TransferenciaService {
	
	@Autowired
	private TransferenciaRepository transferenciaRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
    @Override
    public List<Transferencia> getAllTransferencias() {
        return transferenciaRepository.findAll();
    }

//    @Override
//    public List<Transferencia> getTransferenciasPorConta(Long id) {
//        return transferenciaRepository.findById(id)
//                .map(Collections::singletonList)
//                .orElse(Collections.emptyList());
//    }
    @Override
    public List<Transferencia> getTransferenciasPorConta(Long numeroConta) {
        return transferenciaRepository.findByContaNumeroConta(numeroConta);
    }

    @Override
    public List<Transferencia> getTransferenciasPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return transferenciaRepository.findByDataTransferenciaBetween(dataInicio, dataFim);
    }

    @Override
    public List<Transferencia> getTransferenciasPorOperador(String nomeOperador) {
        return transferenciaRepository.findByNomeOperadorTransacao(nomeOperador);
    }
    
    @Override
    public List<Transferencia> getTransferenciasPorPeriodoEOperador(LocalDateTime dataInicio, LocalDateTime dataFim, String nomeOperador) {
        return transferenciaRepository.findByDataInicioAndDataFimAndNomeOperador(dataInicio, dataFim, nomeOperador);
    }

    @Override
    public Page<Transferencia> getTransferenciasPaginadas(Pageable pageable) {
        return transferenciaRepository.findAll(pageable);
    }
    
    @Override
    public Transferencia criarTransferencia(Transferencia transferencia) {
        // Obtenha a conta de origem e destino com base nos IDs fornecidos na transferência
        Conta contaOrigem = contaRepository.findById(transferencia.getConta().getId())
                .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada"));
        Conta contaDestino = contaRepository.findById(transferencia.getContaDestino().getId())
                .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada"));

        // Realize a transferência
        realizarTransferencia(transferencia, contaOrigem, contaDestino);

        // Salve a transferência no banco de dados
        return transferenciaRepository.save(transferencia);
    }
    
    private void realizarTransferencia(Transferencia transferencia, Conta contaOrigem, Conta contaDestino) {
        // Verifique se a conta de origem tem saldo suficiente para a transferência
        if (contaOrigem.getSaldo() < transferencia.getValor()) {
            throw new RuntimeException("Saldo insuficiente na conta de origem");
        }
        transferencia.setNomeOperadorTransacao(contaDestino.getNome());

        // Realize a subtração do valor da conta de origem
        double novoSaldoOrigem = contaOrigem.getSaldo() - transferencia.getValor();
        contaOrigem.setSaldo(novoSaldoOrigem);
        contaRepository.save(contaOrigem);

        // Realize a adição do valor na conta de destino
        double novoSaldoDestino = contaDestino.getSaldo() + transferencia.getValor();
        contaDestino.setSaldo(novoSaldoDestino);
        contaRepository.save(contaDestino);
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
            case TRANSF_ENTRADA:
                // Atualizar o saldo atual para transferência de entrada
                saldoAtual += valor;
                break;
            case TRANSF_SAIDA:
                // Atualizar o saldo atual para transferência de saída
                saldoAtual -= valor;
                break;
        }
        
        // Arredonde o saldo atual para duas casas decimais
        saldoAtual = Math.round(saldoAtual * 100.0) / 100.0;
        
        return saldoAtual;
    }

    @Override
    public void sacar(Long idConta, double valor) {
        Conta conta = obterContaPorId(idConta);

        if (conta.getSaldo() >= valor) {
            double novoSaldo = conta.getSaldo() - valor;
            conta.setSaldo(novoSaldo);

            // Criar a transferência de saque
            Transferencia transferencia = new Transferencia();
            transferencia.setDataTransferencia(LocalDateTime.now());
            transferencia.setValor(-valor);  // Define o valor como negativo
            transferencia.setTipo(Operation.SAQUE);
            transferencia.setConta(conta);
            transferencia.setNomeOperadorTransacao("Sistema");

			// Adicionar a transferência à lista de transferências da conta
			conta.adicionarTransferencia(transferencia);

			// Salvar as alterações no banco de dados
			contaRepository.save(conta);
			transferenciaRepository.save(transferencia);
		} else {
			throw new IllegalArgumentException("Saldo insuficiente");
		}
	}

	@Override
	public Conta obterContaPorId(Long id) {
		return contaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
	}
}