package br.com.banco.services;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.enums.Operation;
import br.com.banco.repositories.ContaRepository;
import br.com.banco.repositories.TransferenciaRepository;

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
            transferencia.setDataTransferencia(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")));
            transferencia.setValor(-valor);  // Define o valor como negativo
            transferencia.setTipo(Operation.SAQUE);
            transferencia.setConta(conta);
            transferencia.setNomeOperadorTransacao("Sistema");
            conta.adicionarTransferencia(transferencia);

            transferenciaRepository.save(transferencia);
        } else {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
    }
 
    @Override
    public Conta obterContaPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    }
    @Override
    public Conta obterContaPorNome(String nome) {
        return contaRepository.findByNomeIgnoreCaseLike(nome);
    }
    
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
            transferencia.setDataTransferencia(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).withNano(0));
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
    public List<Transferencia> buscarTransacoesPorPeriodoENome(ZonedDateTime dataInicio, ZonedDateTime dataFim, String nome) {
        ZonedDateTime dataInicioCompleta = dataInicio.toLocalDate().atStartOfDay(dataInicio.getZone());
        ZonedDateTime dataFimCompleta = dataFim.toLocalDate().atTime(LocalTime.MAX).atZone(dataFim.getZone());

        return transferenciaRepository.buscarPorPeriodoENome(dataInicioCompleta, dataFimCompleta, nome);
    }
    
    @Override
    public List<Transferencia> buscarTransacoesPorNome(String nome) {
        return transferenciaRepository.buscarPorNome(nome);
    }

    @Override
    public double calcularSaldoTotalPorNome(String nome) {
        if (nome == null) {
            throw new IllegalArgumentException("Esse nome não foi encontrado.");
        }
        try {
            Conta conta = obterContaPorNome(nome);
            if (conta == null) {
                throw new IllegalArgumentException("Essa conta não foi encontrada.");
            }
            double saldo = conta.getSaldo();
            return saldo;
        } catch (Exception e) {
            logger.warn("Ocorreu uma exceção durante o cálculo do saldo: {}", " '" + e.getMessage() + " '");
            return 0.0;
        }
    }

    @Override
    public double calcularSaldoPeriodoPorNome(ZonedDateTime dataInicio, ZonedDateTime dataFim, String nome) {
        List<Transferencia> transacoes;
        
        if (dataInicio == null && dataFim == null) {
            transacoes = buscarTransacoesPorNome(nome);
        } else {
            transacoes = buscarTransacoesPorPeriodoENome(dataInicio, dataFim, nome);
        }
        
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
                case TRANSFERENCIA:
                    saldoPeriodo += valorOperacao; // ou -= valorOperacao, dependendo do fluxo desejado
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
    
    public Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        return errorResponse;
    }
    
    public boolean hasContaByName(String nome) {
        Conta conta = contaRepository.findByNomeIgnoreCaseLike(nome);
        return conta != null;
    }
    
    public boolean hasConta(Long id) {
        return contaRepository.existsById(id);
    }

	public void validarParametros(Long idContaOrigem, Long idContaDestino, double valor, br.com.banco.enums.Operation tipo) {
		if (idContaOrigem == null || idContaDestino == null) {
			throw new IllegalArgumentException("IDs das contas de origem e destino devem ser fornecidos.");
		}
		if (valor <= 0) {
			throw new IllegalArgumentException("O valor da transferência deve ser maior que zero.");
		}
		if (tipo == null) {
			throw new IllegalArgumentException("O tipo de operação deve ser fornecido.");
		}
	}
}