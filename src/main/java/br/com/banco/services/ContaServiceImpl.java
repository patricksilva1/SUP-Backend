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
import br.com.banco.exceptions.ContaException;
import br.com.banco.exceptions.ContaNotFoundException;
import br.com.banco.exceptions.SaldoInsuficienteException;
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

	/**
	 * Creates a new account with the given name.
	 *
	 * @param nome the name of the account to be created
	 * @return the newly created account
	 * @throws IllegalArgumentException if the name is null or empty
	 * @throws ContaException if an error occurs while creating the account
	 */
    @Override
    public Conta criarConta(String nome) throws IllegalArgumentException {
        if (nome == null || nome.isEmpty()) {
        	 logger.warn("Nome inválido: o nome não pode ser nulo ou vazio.");
        }
        try {
            Conta conta = new Conta(nome);
            conta.setDataCriacaoaAdjusted(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).withNano(0));
            conta.setSaldo(0.0);
            return contaRepository.save(conta);
        } catch (Exception e) {
            logger.error("Erro ao criar conta: " + e.getMessage());
            throw new ContaException("Erro ao criar conta.", e);
        }
    }

    /**
     * Deposits the specified amount into the account with the given ID.
     *
     * @param idConta the ID of the account to deposit into
     * @param valor the amount to deposit
     * @throws IllegalArgumentException if the ID or value is invalid
     * @throws ContaNotFoundException if the account with the given ID is not found
     * @throws ContaException if an error occurs while depositing the amount
     */
    @Override
    public void depositar(Long idConta, double valor) throws IllegalArgumentException, ContaNotFoundException {
        if (idConta == null) {
        	logger.warn("ID da conta inválido: o ID não pode ser nulo.");
        }
        if (valor <= 0) {
        	logger.warn("Valor inválido: o valor deve ser maior que zero.");
        }
        Conta conta = obterContaPorId(idConta);
        if (conta == null) {
        	logger.warn("Conta não encontrada para o ID: " + idConta);
        }
        try {
            double novoSaldo = conta.getSaldo() + valor;
            conta.setSaldo(novoSaldo);
        } catch (Exception e) {
            logger.error("Erro ao depositar valor na conta: " + e.getMessage());
            throw new ContaException("Erro ao depositar valor na conta.", e);
        }
    }

    /**
     * Withdraws the specified amount from the account with the given ID.
     *
     * @param idConta the ID of the account to withdraw from
     * @param valor the amount to withdraw
     * @throws IllegalArgumentException if the ID or value is invalid
     * @throws ContaNotFoundException if the account with the given ID is not found
     * @throws SaldoInsuficienteException if the account balance is insufficient for the withdrawal
     * @throws ContaException if an error occurs while withdrawing the amount
     */
    @Override
    public void sacar(Long idConta, double valor) throws IllegalArgumentException, ContaNotFoundException, SaldoInsuficienteException {
        if (idConta == null || idConta <= 0) {
        	logger.warn("ID da conta inválido: o ID não pode ser nulo.");
        }
        if (valor <= 0) {
        	logger.warn("Valor inválido: o valor deve ser maior que zero.");
        }
        Conta conta = obterContaPorId(idConta);
        if (conta == null) {
        	logger.warn("Conta não encontrada para o ID: " + idConta);
        }
        try {
            if (conta.getSaldo() >= valor) {
                double novoSaldo = conta.getSaldo() - valor;
                conta.setSaldo(novoSaldo);
                // Criar a transferência de saque
                Transferencia transferencia = new Transferencia();
                transferencia.setDataTransferencia(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).withNano(0));
                transferencia.setValor(-valor);  // Define o valor como negativo
                transferencia.setTipo(Operation.SAQUE);
                transferencia.setConta(conta);
                transferencia.setNomeOperadorTransacao("Sistema");
                conta.adicionarTransferencia(transferencia);
                transferenciaRepository.save(transferencia);
            } else {
                throw new SaldoInsuficienteException("Saldo insuficiente para o saque.");
            }
        } catch (Exception e) {
            logger.error("Erro ao sacar valor da conta: " + e.getMessage());
            throw new ContaException("Erro ao sacar valor da conta.", e);
        }
    }

    /**
     * Retrieves the account with the specified ID.
     *
     * @param id the ID of the account to retrieve
     * @return the account with the given ID
     * @throws ContaNotFoundException if the account with the given ID is not found
     * @throws IllegalArgumentException if the ID is invalid
     */
	@Override
	public Conta obterContaPorId(Long id) throws ContaNotFoundException {
		if (id == null || id <= 0) {
			logger.warn("ID da conta inválido: o ID não pode ser nulo.");
		}
		return contaRepository.findById(id).orElseThrow(() -> new ContaNotFoundException("Conta não encontrada para o ID: " + id));
	}

	/**
	 * Retrieves the account with the specified name.
	 *
	 * @param nome the name of the account to retrieve
	 * @return the account with the given name
	 * @throws IllegalArgumentException if the name is null
	 */
	@Override
	public Conta obterContaPorNome(String nome) {
	    if (nome == null) {
	        logger.warn("Nome da conta não pode ser nulo");
	    }
	    return contaRepository.findByNomeIgnoreCaseLike(nome);
	}

	/**
	 * 
	 * Transfers funds between two accounts.
	 * 
	 * @param idContaOrigem  The ID of the source account.
	 * @param idContaDestino The ID of the destination account.
	 * @param valor          The amount to transfer.
	 * @param tipo           The type of operation (e.g., online transfer, in-person transfer).
	 * @throws IllegalArgumentException   If the source or destination account IDs are null.
	 * @throws ContaNotFoundException     If either the source or destination account is not found.
	 * @throws SaldoInsuficienteException If the source account does not have sufficient balance to perform the transfer.
	 */
	@Override
	public void transferir(Long idContaOrigem, Long idContaDestino, double valor, Operation tipo) throws IllegalArgumentException, ContaNotFoundException, SaldoInsuficienteException {
	    if (idContaOrigem == null || idContaDestino == null) {
	        logger.warn("IDs das contas inválidos: os IDs não podem ser nulos.");
	    }
	    if (valor <= 0) {
	    	logger.warn("Valor inválido: o valor deve ser maior que zero.");
	    }
	    Conta contaOrigem = obterContaPorId(idContaOrigem);
	    Conta contaDestino = obterContaPorId(idContaDestino);
	    try {
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
	            throw new SaldoInsuficienteException("Saldo insuficiente na conta de origem para realizar a transferência.");
	        }
	    } catch (Exception e) {
	        logger.error("Erro ao transferir valor entre contas: " + e.getMessage());
	        throw new ContaException("Erro ao transferir valor entre contas.", e);
	    }
	}
    
	/**
	 * 
	 * Retrieves a list of transfers within a specified period for a given name.
	 * 
	 * @param dataInicio The start date of the period.
	 * @param dataFim    The end date of the period.
	 * @param nome       The name associated with the transfers.
	 * @return A list of Transferencia objects matching the specified criteria.
	 */
	@Override
	public List<Transferencia> buscarTransacoesPorPeriodoENome(ZonedDateTime dataInicio, ZonedDateTime dataFim, String nome) {
	    if (dataInicio == null || dataFim == null) {
	    	logger.warn("Datas de início e fim devem ser fornecidas");
	    }
	    if (dataInicio.isAfter(dataFim)) {
	    	logger.warn("Data de início deve ser anterior ou igual à data de fim");
	    }
	    ZonedDateTime dataInicioCompleta = dataInicio.toLocalDate().atStartOfDay(dataInicio.getZone());
	    ZonedDateTime dataFimCompleta = dataFim.toLocalDate().atTime(LocalTime.MAX).atZone(dataFim.getZone());
	    return transferenciaRepository.buscarPorPeriodoENome(dataInicioCompleta, dataFimCompleta, nome);
	}
    
	/**
	 * 
	 * Retrieves a list of transfers for a given name.
	 * 
	 * @param nome The name associated with the transfers.
	 * @return A list of Transferencia objects matching the specified name.
	 */
	@Override
	public List<Transferencia> buscarTransacoesPorNome(String nome) {
	    if (nome == null) {
	    	logger.warn("Nome não pode ser nulo");
	    }
	    return transferenciaRepository.buscarPorNome(nome);
	}

	/**
	 * Calculates the total balance for an account with the specified name.
	 * 
	 * @param nome The name associated with the account.
	 * @return The total balance of the account.
	 */
    @Override
    public double calcularSaldoTotalPorNome(String nome) {
        if (nome == null) {
            logger.warn("Esse nome não foi encontrado.");
        }
        try {
            Conta conta = obterContaPorNome(nome);
            if (conta == null) {
                logger.warn("Essa conta não foi encontrada.");
            }
            double saldo = conta.getSaldo();
            return saldo;
        } catch (ContaNotFoundException e) {
            logger.warn("Conta não encontrada para o nome '{}'", nome);
            return 0.0;
        } catch (Exception e) {
            logger.warn("Verifique o cálculo do saldo para o nome '{}': {}", nome, e.getMessage());
            return 0.0;
        }
    }

	/**
	 * Calculates the balance for an account within a specified period and
	 * associated with a given name.
	 * 
	 * @param dataInicio The start date of the period. If null, considers all transfers for the specified name.
	 * @param dataFim    The end date of the period. If null, considers all transfers for the specified name.
	 * @param nome       The name associated with the account.
	 * @return The balance of the account within the specified period.
	 */
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
                    break;
            }
        }

        // Formatar o valor com duas casas decimais
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String saldoPeriodoFormatado = decimalFormat.format(saldoPeriodo);
        saldoPeriodo = Double.parseDouble(saldoPeriodoFormatado);

        return saldoPeriodo;
    }
    
	/**
	 * Creates an error response with the specified error message.
	 * 
	 * @param errorMessage The error message to be included in the response.
	 * @return A Map object representing the error response.
	 */
    public Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        return errorResponse;
    }

	/**
	 * 
	 * Checks if an account exists with the specified name.
	 * 
	 * @param nome The name to check.
	 * @return True if an account with the specified name exists, false otherwise.
	 */
	public boolean hasContaByName(String nome) {
		if (nome == null) {
			logger.warn("Nome não pode ser nulo");
		}
        Conta conta = contaRepository.findByNomeIgnoreCaseLike(nome);
        return conta != null;
    }
    
	/**
	 * Checks if an account exists with the specified ID.
	 * 
	 * @param id The ID to check.
	 * @return True if an account with the specified ID exists, false otherwise.
	 */
	public boolean hasConta(Long id) {
		if (id == null) {
			logger.warn("O Id não pode ser nulo");
		}
    	if (id <= 0) {
			logger.warn("O Id deve ser um número positivo.");
		}
        return contaRepository.existsById(id);
    }

	/**
	 * Validates the parameters for a transfer operation.
	 * 
	 * @param idContaOrigem  The ID of the source account.
	 * @param idContaDestino The ID of the destination account.
	 * @param valor          The amount to transfer.
	 * @param tipo           The type of operation.
	 * @throws IllegalArgumentException if any of the parameters are invalid.
	 */
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

	@Override
	public Map<String, ZonedDateTime> encontrarPrimeiraEUltimaDataPorNomeOperador(String nomeOperador) {
		// TODO Auto-generated method stub
		return null;
	}
}