package br.com.banco.services;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.enums.Operation;
import br.com.banco.repositories.ContaRepository;
import br.com.banco.repositories.TransferenciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class ContaServiceImpl implements ContaService {
	private static final Logger logger = LoggerFactory.getLogger(ContaServiceImpl.class);

	private final ContaRepository contaRepository;

	private final TransferenciaRepository transferenciaRepository;

	public ContaServiceImpl(ContaRepository contaRepository, TransferenciaRepository transferenciaRepository) {
		this.contaRepository = contaRepository;
		this.transferenciaRepository = transferenciaRepository;
	}

    @Override
    public Conta criarConta(String nome) {
        Conta conta = new Conta(nome);
        conta.setSaldo(0.0);
        return contaRepository.save(conta);
    }

    @Override
    public void depositar(Long idConta, double valor) {
        Conta conta = obterContaPorId(idConta);
        double novoSaldo = conta.getSaldo() + valor;
        conta.setSaldo(novoSaldo);
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
//            contaRepository.save(conta);
            transferenciaRepository.save(transferencia);
        } else {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
    }



    /**
     * FUNCIONANDO
     */
//    @Override
//    public void transferir(Long idContaOrigem, Long idContaDestino, double valor) {
//        Conta contaOrigem = obterContaPorId(idContaOrigem);
//        Conta contaDestino = obterContaPorId(idContaDestino);
//        double saldoOrigem = contaOrigem.getSaldo();
//        if (saldoOrigem >= valor) {
//            double novoSaldoOrigem = saldoOrigem - valor;
//            contaOrigem.setSaldo(novoSaldoOrigem);
//            double novoSaldoDestino = contaDestino.getSaldo() + valor;
//            contaDestino.setSaldo(novoSaldoDestino);
//        } else {
//            throw new IllegalArgumentException("Saldo insuficiente");
//        }
//    }
    
    @Override
    public Conta obterContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    }
//
//    private Conta obterContaPorId(Long idConta) {
//        return contaRepository.findById(idConta)
//                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
//    }
    
    @Override
    public void transferir(Long idContaOrigem, Long idContaDestino, double valor, Operation tipo) {
        Conta contaOrigem = obterContaPorId(idContaOrigem);
        Conta contaDestino = obterContaPorId(idContaDestino);

        if (contaOrigem.getSaldo() >= valor) {
            // Atualizar o saldo da conta de origem
            double novoSaldoOrigem = contaOrigem.getSaldo() - valor;
            contaOrigem.setSaldo(novoSaldoOrigem);

            // Atualizar o saldo da conta de destino
            double novoSaldoDestino = contaDestino.getSaldo() + valor;
            contaDestino.setSaldo(novoSaldoDestino);

            // Criar a transferência
            Transferencia transferencia = new Transferencia();
            transferencia.setDataTransferencia(LocalDateTime.now());
            transferencia.setValor(valor);
            transferencia.setTipo(tipo);
            transferencia.setConta(contaOrigem);
            transferencia.setContaDestino(contaDestino);
            transferencia.setNomeOperadorTransacao(contaDestino.getNome());

            // Adicionar a transferência à lista de transferências da conta de origem
            contaOrigem.adicionarTransferencia(transferencia);

            // Salvar as alterações no banco de dados
            contaRepository.save(contaOrigem);
            contaRepository.save(contaDestino);
        } else {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
    }
    
    @Override
    public List<Transferencia> buscarTransacoesPorPeriodoENome(LocalDateTime dataInicio, LocalDateTime dataFim, String nome) {
        return transferenciaRepository.buscarPorPeriodoENome(dataInicio, dataFim, nome);
    }

	@Override
	public double calcularSaldoTotalPorNome(String nome) {
		if (nome == null) {
			throw new IllegalArgumentException("Esse nome não foi encontrado.");
		}
		try {
			Optional<Conta> optionalConta = contaRepository.findByNome(nome);

			if (optionalConta.isEmpty()) {
				throw new IllegalArgumentException("Essa conta não foi encontrada.");
			}
			
			Conta conta = optionalConta.get();
			return conta.getSaldo();
		} catch (Exception e) {
			logger.error("Ocorreu uma exceção durante o cálculo do saldo: {}", " '" + e.getMessage() + " '");

			return 0.0;
		}
	}

    @Override
    public double calcularSaldoPeriodoPorNome(LocalDateTime dataInicio, LocalDateTime dataFim, String nome) {
        List<Transferencia> transacoes = buscarTransacoesPorPeriodoENome(dataInicio, dataFim, nome);
        double saldoPeriodo = 0.0;

        for (Transferencia transferencia : transacoes) {
            Operation tipoOperacao = transferencia.getTipo();
            double valorOperacao = transferencia.getValor();

            switch (tipoOperacao) {
                case DEPOSITO:
                case TRANSF_ENTRADA:
                    saldoPeriodo += valorOperacao;
                    break;
                case SAQUE:
                case TRANSF_SAIDA:
                    saldoPeriodo -= valorOperacao;
                    break;
                default:
                    // Lida com outros tipos de operações, se necessário
                    break;
            }
        }
        
     // Formatar o valor com duas casas decimais
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String saldoPeriodoFormatado = decimalFormat.format(saldoPeriodo);
        saldoPeriodo = Double.parseDouble(saldoPeriodoFormatado);


        return saldoPeriodo;
    }   
}