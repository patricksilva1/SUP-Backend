package br.com.banco.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.banco.controllers.TransferenciaController;
import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.enums.Operation;
import br.com.banco.exceptions.SaldoInsuficienteException;
import br.com.banco.exceptions.SaldoNegativoException;
import br.com.banco.exceptions.TransferenciaException;
import br.com.banco.repositories.ContaRepository;
import br.com.banco.repositories.TransferenciaRepository;

@Service
public class TransferenciaServiceImpl implements TransferenciaService {
	private static final Logger logger = LoggerFactory.getLogger(TransferenciaController.class);
	
	@Autowired
	private TransferenciaRepository transferenciaRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	/**
	 * Retrieves all transferências from the repository.
	 * 
	 * @return a list of Transferencia objects representing all transferências.
	 * @throws TransferenciaException if there is an error retrieving the transferências.
	 */
	@Override
	public List<Transferencia> getAllTransferencias() throws TransferenciaException {
	    try {
	        List<Transferencia> transferencias = transferenciaRepository.findAll();
	        if (transferencias.isEmpty()) {
	        	logger.warn("A lista de transferências está vazia.");
	        }
	        for (Transferencia transferencia : transferencias) {
	            if (transferencia == null) {
	            	logger.warn("Transferência nula encontrada na lista.");
	            }
	        }

	        return Collections.unmodifiableList(transferencias);
	    } catch (TransferenciaException e) {
	        logger.error("Erro ao obter todas as transferências: " + e.getMessage());
	        throw e;
	    } catch (Exception e) {
	        logger.error("Erro ao obter todas as transferências: " + e.getMessage());
	        throw new TransferenciaException("Erro ao obter todas as transferências.", e);
	    }
	}

	/**
	 * Retrieves transferências by account number.
	 * 
	 * @param numeroConta the account number to retrieve transferências for.
	 * @return a list of Transferencia objects representing transferências for the specified account.
	 * @throws TransferenciaException if the account ID is invalid or there is an error retrieving the transferências.
	 */
	@Override
	public List<Transferencia> getTransferenciasPorConta(Long numeroConta) {
		if (numeroConta == null || numeroConta <= 0) {
	        throw new TransferenciaException("ID da conta inválido: " + numeroConta);
	    }
	    try {
	        List<Transferencia> transferencias = transferenciaRepository.findByContaNumeroConta(numeroConta);
	        if (transferencias == null) {
	            throw new TransferenciaException("A lista de transferências por conta retornou nula.");
	        }
	        for (Transferencia transferencia : transferencias) {
	            if (transferencia == null) {
	                throw new TransferenciaException("Transferência nula encontrada na lista por conta.");
	            }
	        }

	        return Collections.unmodifiableList(transferencias);
	    } catch (TransferenciaException e) {
	        logger.error("Erro ao obter transferências por conta: " + e.getMessage());
	        throw e;
	    } catch (Exception e) {
	        logger.error("Erro ao obter transferências por conta: " + e.getMessage());
	        throw new TransferenciaException("Erro ao obter transferências por conta.", e);
	    }
	}

	/**
	 * Retrieves a list of Transferencia objects for a specified period.
	 * 
	 * @param dataInicio The start date of the period.
	 * @param dataFim    The end date of the period.
	 * @return A list of Transferencia objects within the specified period.
	 * @throws TransferenciaException If an error occurs while retrieving the transferências.
	 */
	@Override
	public List<Transferencia> getTransferenciasPorPeriodo(ZonedDateTime dataInicio, ZonedDateTime dataFim) {
		if (dataInicio == null || dataFim == null) {
			logger.warn("Período de datas inválido: as datas de início e fim devem ser fornecidas.");
			return null; 
		}
		if (dataInicio.isAfter(dataFim)) {
			logger.warn("Período de datas inválido: a data de início deve ser anterior à data de fim.");
			return null; 
		}
	    try {
	        List<Transferencia> transferencias = transferenciaRepository.findByDataTransferenciaBetween(dataInicio, dataFim);
	        if (transferencias == null) {
	            throw new TransferenciaException("A lista de transferências por período retornou nula.");
	        }
	        for (Transferencia transferencia : transferencias) {
	            if (transferencia == null) {
	                throw new TransferenciaException("Transferência nula encontrada na lista por período.");
	            }
	        }

	        return Collections.unmodifiableList(transferencias);
	    } catch (TransferenciaException e) {
	        logger.error("Erro ao obter transferências por período: " + e.getMessage());
	        throw e;
	    } catch (Exception e) {
	        logger.error("Erro ao obter transferências por período: " + e.getMessage());
	        throw new TransferenciaException("Erro ao obter transferências por período.", e);
	    }
	}

	/**
	 * Retrieves a list of Transferencia objects for a given operator.
	 *
	 * @param nomeOperador the name of the operator
	 * @return a list of Transferencia objects matching the given operator
	 * @throws IllegalArgumentException if the name of the operator is null or empty
	 * @throws TransferenciaException   if an error occurs while retrieving the transferencias
	 */
	@Override
	public List<Transferencia> getTransferenciasPorOperador(String nomeOperador) {
		try {	        
			return transferenciaRepository.findByNomeOperadorTransacao(nomeOperador);
		} catch (Exception e) {
			logger.warn("Erro ao obter as transferências por operador.");
			throw new TransferenciaException("Erro ao obter as transferências por operador.");
		}
	}
	
	/**
     * Retrieves a list of Transferencia objects for a given period and operator.
     *
     * @param dataInicio   the start date of the period
     * @param dataFim      the end date of the period
     * @param nomeOperador the name of the operator
     * @return a list of Transferencia objects matching the given period and operator
     * @throws IllegalArgumentException if the start and end dates are not provided correctly,
     *                                  or if the name of the operator is null or empty
     * @throws TransferenciaException    if an error occurs while retrieving the transferencias
     */
    @Override
    public List<Transferencia> getTransferenciasPorPeriodoEOperador(ZonedDateTime dataInicio, ZonedDateTime dataFim, String nomeOperador) {
        try {
            if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            	logger.warn("As datas de início e fim devem ser fornecidas corretamente.");
            }

            return transferenciaRepository.findByDataInicioAndDataFimAndNomeOperador(dataInicio, dataFim, nomeOperador);
        } catch (IllegalArgumentException e) {
        	logger.warn("Parâmetros inválidos fornecidos ao obter as transferências por período e operador.", e);
            throw e;
        } catch (Exception e) {
        	logger.error("Erro ao obter as transferências por período e operador.", e);
            throw new TransferenciaException("Erro ao obter as transferências por período e operador.", e);
        }
    }

    /**
     * Retrieves a page of Transferencia objects based on the provided Pageable.
     *
     * @param pageable the Pageable object specifying the page number, size, sorting, etc.
     * @return a Page object containing the transferencias for the specified page
     * @throws IllegalArgumentException if the pageable object is null
     * @throws TransferenciaException if an error occurs while retrieving the transferencias
     */
    @Override
    public Page<Transferencia> getTransferenciasPaginadas(Pageable pageable) {
        try {
            if (pageable == null) {
            	logger.warn("O objeto Pageable não pode ser nulo.");
            }
            return transferenciaRepository.findAll(pageable);
        } catch (IllegalArgumentException e) {
        	logger.warn("Pageable inválido fornecido ao obter as transferências paginadas.", e);
            throw e;
        } catch (Exception e) {
        	logger.error("Erro ao obter as transferências paginadas.", e);
            throw new TransferenciaException("Erro ao obter as transferências paginadas.", e);
        }
    }
    
	/**
	 * 
	 * Creates a new transfer in the system.
	 * 
	 * @param transferencia The transfer object containing the details of the transfer.
	 * @return The created transfer object.
	 * @throws TransferenciaException If an error occurs while creating the transfer.
	 */
    @Override
    public Transferencia criarTransferencia(Transferencia transferencia) {
        try {
            Conta contaOrigem = contaRepository.findById(transferencia.getConta().getId()).orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));
            Conta contaDestino = contaRepository.findById(transferencia.getContaDestino().getId()).orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));
            realizarTransferencia(transferencia, contaOrigem, contaDestino);

            return transferenciaRepository.save(transferencia);
		} catch (TransferenciaException e) {
			logger.error("Erro ao obter todas as transferências: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Ocorreu um erro ao criar a transferência: " + e.getMessage());
			throw new TransferenciaException("Erro ao criar a transferência", e);
		}
    }
   
    /**
     * Updates a transfer by its ID.
     *
     * @param id the ID of the transfer to be updated
     * @param transferencia the new data for the transfer
     * @return the updated transfer
     * @throws TransferenciaException if an error occurs while updating the transfer
     */
	@Override
	public Transferencia atualizarTransferencia(Long id, Transferencia transferencia) {
		try {
			Transferencia transferenciaExistente = transferenciaRepository.findById(id).orElseThrow(() -> new RuntimeException("Transferência não encontrada"));
			if (transferencia.getValor() != null) {
				transferenciaExistente.setValor(transferencia.getValor());
			}
			if (transferencia.getTipo() != null) {
				transferenciaExistente.setTipo(transferencia.getTipo());
			}
			if (transferencia.getNomeOperadorTransacao() != null) {
				transferenciaExistente.setNomeOperadorTransacao(transferencia.getNomeOperadorTransacao());
			}
			Double saldoAtual = calcularSaldoAtual(transferenciaExistente);
			transferenciaExistente.setSaldoAtual(saldoAtual);

			return transferenciaRepository.save(transferenciaExistente);
		} catch (Exception e) {
			logger.error("Ocorreu um erro ao atualizar a transferência: " + e.getMessage());
			throw new TransferenciaException("Erro ao atualizar a transferência", e);
		}
	}

	/**
	 * Withdraws an amount from an account.
	 *
	 * @param idConta the ID of the account
	 * @param valor the amount to be withdrawn
	 * @throws IllegalArgumentException if the withdrawal amount is invalid or if the balance is insufficient
	 * @throws TransferenciaException if an error occurs while calculating the current balance or saving the transfer data
	 */
	@Override
	public void sacar(Long idConta, double valor) {
		if (valor <= 0) {
			logger.warn("Valor de saque inválido");
		}
		try {
			Conta conta = obterContaPorId(idConta);
			if (conta.getSaldo() >= valor) {
				double novoSaldo = conta.getSaldo() - valor;
				conta.setSaldo(novoSaldo);

				Transferencia transferencia = new Transferencia();
				transferencia.setDataTransferencia(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")));
				transferencia.setValor(-valor); // Definir o valor como negativo (saque)
				transferencia.setTipo(Operation.SAQUE);
				transferencia.setConta(conta);
				transferencia.setNomeOperadorTransacao("Sistema");

				conta.adicionarTransferencia(transferencia);

				contaRepository.save(conta);
				transferenciaRepository.save(transferencia);
			} else {
				logger.warn("Saldo insuficiente");
			}
		} catch (TransferenciaException e) {
			logger.error("Ocorreu um erro ao calcular o saldo atual: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Ocorreu um erro ao calcular o saldo atual: " + e.getMessage());
			throw new TransferenciaException("Erro ao calcular o saldo atual", e);
		}
	}

	/**
	 * Retrieves an account by its ID.
	 *
	 * @param id the ID of the account to retrieve
	 * @return the found account
	 * @throws IllegalArgumentException if the account is not found
	 */
	@Override
	public Conta obterContaPorId(Long id) {
		return contaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
	}
	
	/**
	 * Calculates the current balance based on the transfer.
	 *
	 * @param transferencia the transfer for which to calculate the current balance
	 * @return the calculated current balance
	 * @throws TransferenciaException if an error occurs while calculating the current balance
	 */
	private Double calcularSaldoAtual(Transferencia transferencia) {
		try {
			Conta conta = transferencia.getConta();
			if (conta == null) {
				logger.warn("Conta não encontrada na transferência");
				return null;
			}
			Double saldoAtual = conta.getSaldo();
			Operation tipo = transferencia.getTipo();
			if (tipo == null) {
				logger.warn("Tipo de operação não especificado na transferência");
				return null;
			}
			Double valor = transferencia.getValor();
			if (valor == null) {
				logger.warn("Valor não especificado na transferência");
				return null;
			}
			switch (tipo) {
			case DEPOSITO:
				saldoAtual += valor;
				break;
			case SAQUE:
				saldoAtual -= valor;
				break;
			case TRANSF_ENTRADA:
				saldoAtual += valor;
				break;
			case TRANSF_SAIDA:
				saldoAtual -= valor;
				break;
			default:
				throw new TransferenciaException("Tipo de operação inválido na transferência");
			}
			saldoAtual = Math.round(saldoAtual * 100.0) / 100.0;

			return saldoAtual;
		} catch (TransferenciaException e) {
			logger.error("Ocorreu um erro ao calcular o saldo atual: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Ocorreu um erro ao calcular o saldo atual: " + e.getMessage());
			throw new TransferenciaException("Erro ao calcular o saldo atual", e);
		}
	}

	/**
	 * Performs a transfer between accounts.
	 *
	 * @param transferencia the transfer object containing the transfer details
	 * @param contaOrigem the source account from which the transfer is made
	 * @param contaDestino the destination account to which the transfer is made
	 * @throws SaldoInsuficienteException if the source account has insufficient balance for the transfer
	 * @throws SaldoNegativoException if the transfer results in a negative balance in either the source or destination account
	 * @throws TransferenciaException if an error occurs while performing the transfer
	 */
	private void realizarTransferencia(Transferencia transferencia, Conta contaOrigem, Conta contaDestino) {
		try {
			if (contaOrigem.getSaldo() < transferencia.getValor()) {
				logger.warn("Saldo insuficiente na conta de origem");
			}
			if (transferencia.getTipo() == Operation.TRANSF_SAIDA) {
				transferencia.setNomeOperadorTransacao(contaDestino.getNome());
			} else if (transferencia.getTipo() == Operation.TRANSF_ENTRADA) {
				transferencia.setNomeOperadorTransacao(contaOrigem.getNome());
			}
			// Realizar a subtração do valor da conta de origem
			double novoSaldoOrigem = contaOrigem.getSaldo() - transferencia.getValor();
			if (novoSaldoOrigem < 0) {
				logger.warn("Saldo negativo na conta de origem após a transferência");
			}
			contaOrigem.setSaldo(novoSaldoOrigem);
			contaRepository.save(contaOrigem);

			// Realizar a adição do valor na conta de destino
			double novoSaldoDestino = contaDestino.getSaldo() + transferencia.getValor();
			if (novoSaldoDestino < 0) {
				logger.warn("Saldo negativo na conta de destino após a transferência");
			}
			contaDestino.setSaldo(novoSaldoDestino);
			contaRepository.save(contaDestino);
		} catch (SaldoInsuficienteException e) {
			logger.error("Erro ao realizar a transferência: " + e.getMessage());
			throw e;
		} catch (SaldoNegativoException e) {
			logger.error("Erro ao realizar a transferência: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Ocorreu um erro ao realizar a transferência: " + e.getMessage());
			throw new TransferenciaException("Erro ao realizar a transferência", e);
		}
	}

	// Big O(1) because of date size.
	/**
	 * Obtém a primeira e a última data de um operador com base no nome do operador.
	 *
	 * @param nomeOperador o nome do operador a ser pesquisado
	 * @return um map contendo a primeira e a última data do operador formatadas como strings,
	 *         ou um map vazio se nenhum resultado for encontrado,
	 *         ou null se a primeira ou a última data forem nulas
	 */
	public Map<String, String> getPrimeiraEUltimaDataPorNomeOperador(String nomeOperador) {
		Map<String, ZonedDateTime> result = transferenciaRepository.findPrimeiraEUltimaDataPorNomeOperador(nomeOperador);
		if (result == null || result.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> datas = new HashMap<>();
		ZonedDateTime primeiraData = result.get("primeiraData");
		ZonedDateTime ultimaData = result.get("ultimaData");
		if(primeiraData == null) {
			return null;
		}
		if(ultimaData == null) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String primeiraDataFormatada = primeiraData.format(formatter);
		String ultimaDataFormatada = ultimaData.format(formatter);

		datas.put("primeiraData", primeiraDataFormatada);
		datas.put("ultimaData", ultimaDataFormatada);

		return datas;
	}

	/**
	 * Checks if a date string is in a valid format "dd/MM/yyyy".
	 *
	 * @param date the date string to be checked
	 * @return true if the string is in the valid format, false otherwise
	 */
	public boolean isValidDateFormat(String date) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    try {
	        LocalDate.parse(date, formatter);
	        return true;
	    } catch (DateTimeParseException e) {
	        return false;
	    }
	}
}