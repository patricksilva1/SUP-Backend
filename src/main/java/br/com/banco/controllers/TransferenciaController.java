package br.com.banco.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.banco.entities.Conta;
import br.com.banco.entities.Transferencia;
import br.com.banco.exceptions.DataInvalidaException;
import br.com.banco.exceptions.InvalidPageException;
import br.com.banco.exceptions.NomeVazioException;
import br.com.banco.exceptions.TransferenciaException;
import br.com.banco.exceptions.TransferenciaPaginadaException;
import br.com.banco.repositories.TransferenciaRepository;
import br.com.banco.services.ContaService;
import br.com.banco.services.TransferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "TRANSFERENCIAS", description = "Endpoints Management.")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/transfers")
public class TransferenciaController {

	@Autowired
	private TransferenciaRepository transferenciaRepository;
	
	@Autowired
	private TransferenciaService transferenciaService;
	
	@Autowired
	private ContaService contaService;

	private static final Logger logger = LoggerFactory.getLogger(TransferenciaController.class);

	// "1. A sua api deve fornecer os dados de transferência de acordo com o número da conta bacária."
	/**
	 * Retrieves all transfers related to a specific account number.
	 * 
	 * @param numeroConta The account number.
	 * @return A ResponseEntity containing a list of Transferencia objects. If no
	 *         transfers are found, returns an empty response with HTTP status 204
	 *         (No Content).
	 */
	@Operation(summary = "Retorna todas as transferências relacionadas a um número de conta específico.", description = "Retornar todas as transferências relacionadas a um número de conta específico.")
	@GetMapping("/conta/{numeroConta}")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Transfers found"), @ApiResponse(responseCode = "204", description = "No transfers found") })
	public ResponseEntity<List<Transferencia>> getTransferenciasPorConta(
			@Parameter(description = "Número da conta", example = "12345") @PathVariable Long numeroConta) {
		if (numeroConta == null || numeroConta <= 0) {
			return ResponseEntity.badRequest().build();
		}
		List<Transferencia> transferencias = transferenciaService.getTransferenciasPorConta(numeroConta);

		return (transferencias != null && !transferencias.isEmpty()) ? ResponseEntity.ok(transferencias) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	// "2. Caso não seja informado nenhum filtro, retornar todos os dados de transferência."
	// Retornar todas as transferencias existentes.
	/**
	 * Retrieves all transfers without any filters.
	 *
	 * @return ResponseEntity containing the list of Transferencia or an empty response if there are no transfers.
	 *         In case of an error, returns an error response with HTTP status 500 (Internal Server Error).
	 */
	@Operation(summary = "Retorna todas as transferências sem nenhum filtro.", description = "Retornar todas as transferências sem um filtro especifico.")
	@GetMapping()
	public ResponseEntity<List<Transferencia>> getAllTransferencias() {
		try {
			List<Transferencia> transferencias = transferenciaService.getAllTransferencias();
			return (transferencias != null && !transferencias.isEmpty()) ? ResponseEntity.ok(transferencias) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
		    logger.error("Erro ao obter as transferências", e);
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// "3. Caso seja informado um período de tempo, retornar todas as transferências relacionadas à aquele período de tempo."
	// Big O(n)
	/**
	 * Retrieves all transfers within a specified time period.
	 *
	 * @param dataInicio The start date of the period (format: dd/MM/yyyy).
	 * @param dataFim The end date of the period (format: dd/MM/yyyy).
	 * @return ResponseEntity containing the list of transfers or an empty response (HttpStatus.NO_CONTENT) if there are no transfers in the period.
	 *         In case of a bad request, returns ResponseEntity with BadRequest status.
	 *         In case of internal server error, returns ResponseEntity with Internal Server Error status (HttpStatus.INTERNAL_SERVER_ERROR).
	 */
	@Operation(summary = "Retorna todas as transferências dentro de um período de tempo especificado.", description = "Retornar todas as transferências dentro de um período de tempo especificado.")
	@GetMapping("/periodo")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Successfully retrieved transfers"),
	        @ApiResponse(responseCode = "204", description = "No transfers found in the specified period"),
	        @ApiResponse(responseCode = "400", description = "Bad request"),
	        @ApiResponse(responseCode = "500", description = "Internal server error")})
	public ResponseEntity<List<Transferencia>> getTransferenciasPorPeriodo(
	        @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") String dataInicio,
	        @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") String dataFim) {
		if (dataInicio == null || dataFim == null) {
			logger.warn("Período de datas inválido: as datas de início e fim devem ser fornecidas.");
			return null; // Ou retorne uma lista vazia, dependendo do comportamento desejado
		}
		try {
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	        LocalDate parsedDataInicio = LocalDate.parse(dataInicio, inputFormatter);
	        LocalDate parsedDataFim = LocalDate.parse(dataFim, inputFormatter);

	        ZonedDateTime dataInicioCompleta = parsedDataInicio.atStartOfDay(ZoneId.systemDefault());
	        ZonedDateTime dataFimCompleta = parsedDataFim.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
	        if(dataInicioCompleta.isAfter(dataFimCompleta)) {
				logger.warn("A Data Inicial é Posterior a Data Fim");
	            return ResponseEntity.badRequest().build();
			}
	        List<Transferencia> transferencias = transferenciaService.getTransferenciasPorPeriodo(dataInicioCompleta, dataFimCompleta);

			return (transferencias != null && !transferencias.isEmpty()) ? ResponseEntity.ok(transferencias) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (DateTimeParseException e) {
			logger.warn("Data inválida fornecida pelo usuário");
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			logger.error("Error: Erro ao obter as transferências por período", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

    // "4. Caso seja informado o nome do operador da transação, retornar todas as transferências relacionados à aquele operador."
	/**
	 * Retrieves all transfers related to a specific operator.
	 *
	 * @param nomeOperador The name of the operator.
	 * @return A ResponseEntity containing a list of Transferencia objects if transfers are found,
	 *         or an appropriate error response if the name is null or empty or if no transfers are found.
	 */
	@Operation(summary = "Retorna todas as transferências relacionadas a um operador específico.", description = "Retornar todas as transferências relacionadas a um operador específico.")
	@GetMapping("/operador")
	public ResponseEntity<List<Transferencia>> getTransferenciasPorOperador(@Parameter(description = "Nome do operador", example = "Patrick") @RequestParam String nomeOperador) {
		try {
			if (nomeOperador == null || nomeOperador.isEmpty()) {
				return ResponseEntity.badRequest().build();
			}
			List<Transferencia> nome = transferenciaRepository.findByNomeOperadorTransacao(nomeOperador);
			if (nome.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			Optional<List<Transferencia>> transferencias = Optional.ofNullable(transferenciaService.getTransferenciasPorOperador(nomeOperador));

			return (transferencias != null && !transferencias.isEmpty()) ? ResponseEntity.ok(transferencias.get()) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (Exception e) {
			logger.error("Ocorreu um erro ao obter as transferências do operador: {}", e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// "5. Caso todos os filtros sejam informados, retornar todas as transferências com base no período de tempo informado e o nome do operador."
	// Big O(n)
	/**
	 * Retrieves all transfers based on the specified time period and operator.
	 *
	 * @param dataInicio The start date of the period. Format: dd/MM/yyyy
	 * @param dataFim    The end date of the period. Format: dd/MM/yyyy
	 * @param nomeOperador The name of the operator.
	 * @return A ResponseEntity containing a list of Transferencia objects if transfers exist within the specified period and operator,
	 *         or a ResponseEntity with HTTP status NO_CONTENT if no transfers are found.
	 * @throws TransferenciaException if an error occurs while retrieving the transfers.
	 */
    @Operation(summary = "Retorna todas as transferências com base no período de tempo e operador especificados.", description = "Retornar todas as transferências com base no período de tempo e operador especificados")
    @GetMapping("/periodo-operador")
    public ResponseEntity<List<Transferencia>> getTransferenciasPorPeriodoEOperador(
            @Parameter(description = "Data de início do período", example = "dd/MM/yyyy")
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") String dataInicio,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") String dataFim,
            @Parameter(description = "Nome do operador", example = "Patrick") @RequestParam String nomeOperador) {
    	
	    if (!transferenciaService.isValidDateFormat(dataInicio) || !transferenciaService.isValidDateFormat(dataFim)) {
	        logger.warn("Data inválida fornecida pelo usuário");
	        return ResponseEntity.badRequest().build();
	    }
        try {
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			ZonedDateTime dataInicioCompleta = LocalDate.parse(dataInicio, formatter).atStartOfDay(ZoneId.systemDefault());
			ZonedDateTime dataFimCompleta = LocalDate.parse(dataFim, formatter).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

			List<Transferencia> transferencias = transferenciaService.getTransferenciasPorPeriodoEOperador(dataInicioCompleta, dataFimCompleta, nomeOperador);

			return (transferencias != null && !transferencias.isEmpty()) ? ResponseEntity.ok(transferencias) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Erro ao obter transferências por período e operador", e);
            throw new TransferenciaException("Exception: Erro ao obter transferências por período e operador", e);
        }
    }

	// Obter todas as transferencias com limite por pagina.
	// Big O(n)
    /**
     * Returns paginated results of transfers.
     *
     * @param pagina        The page number.
     * @param tamanhoPagina The page size.
     * @return A ResponseEntity containing a Page of Transferencia objects if transfers exist,
     *         or a ResponseEntity with HTTP status NO_CONTENT if no transfers are found.
     * @throws IllegalArgumentException if the page number or page size is invalid.
     */
	@Operation(summary = "Retorna resultados paginados das transferências.", description = "Retornar resultados paginados das transferências.")
	@GetMapping("/paginadas")
	public ResponseEntity<Page<Transferencia>> getTransferenciasPaginadas(
            @Parameter(description = "O número da página", example = "0") @RequestParam int pagina,
            @Parameter(description = "O tamanho da página", example = "10") @RequestParam int tamanhoPagina) {
		try {
			if (pagina < 0 || tamanhoPagina <= 0) {
				logger.warn("Caro usuário, você inseriu uma paginação errada.");
				throw new IllegalArgumentException("Exception: Número de página ou tamanho de página inválido.");
			}
			Pageable pageable = PageRequest.of(pagina, tamanhoPagina);
			Page<Transferencia> transferenciasPaginadas = transferenciaService.getTransferenciasPaginadas(pageable);

			return (transferenciasPaginadas != null && transferenciasPaginadas.hasContent()) ? ResponseEntity.ok(transferenciasPaginadas) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (IllegalArgumentException e) {
			logger.error("Número de página ou tamanho de página inválido", e);
			throw new InvalidPageException("Exception: Número de página ou tamanho de página inválido.", e);
		} catch (Exception e) {
			logger.error("Erro ao recuperar transferências paginadas", e);
			throw new TransferenciaPaginadaException("Exception: Erro ao recuperar transferências paginadas", e);
		}
	}
	
	// Pegar todas as transacoes em um periodo pelo nome.
	// Big O(n)
	/**
	 * Retrieves all transactions within a specified period by name.
	 *
	 * @param nome       The name to search for transactions.
	 * @param dataInicio The start date of the period. Format: dd/MM/yyyy.
	 * @param dataFim    The end date of the period. Format: dd/MM/yyyy.
	 * @return A ResponseEntity containing a list of Transferencia objects if transactions exist within the specified period and name.
	 * @throws NomeVazioException     if the name is empty or null.
	 * @throws DataInvalidaException  if the start date is after the end date.
	 * @throws TransferenciaException if an error occurs while retrieving the transactions.
	 */
	@Operation(summary = "Retorna resultados das transferências por Periodo e se a Conta existe no banco.", description = "Retornar resultados das transferências por Periodo e se a Conta existe no banco.")
	@GetMapping("/transacoes")
	public ResponseEntity<List<Transferencia>> getTransacoesPorPeriodoENomeESeContaExiste(@RequestParam String nome,
			@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") String dataInicio,
			@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") String dataFim) {
		
		if (!transferenciaService.isValidDateFormat(dataInicio) || !transferenciaService.isValidDateFormat(dataFim)) {
			logger.warn("Data inválida fornecida pelo usuário");
			return ResponseEntity.badRequest().build();
		}
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			ZonedDateTime dataInicioCompleta = LocalDate.parse(dataInicio, formatter).atStartOfDay(ZoneId.systemDefault());
			ZonedDateTime dataFimCompleta = LocalDate.parse(dataFim, formatter).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

			if (nome == null || nome.isEmpty()) {
				throw new NomeVazioException("Exception: O nome não pode estar vazio");
			}
			if(dataInicioCompleta.isAfter(dataFimCompleta)) {
				logger.warn("A Data Inicial é Posterior a Data Fim");
	            return ResponseEntity.badRequest().build();
			}
			List<Transferencia> transacoes = contaService.buscarTransacoesPorPeriodoENome(dataInicioCompleta, dataFimCompleta, nome);
			
			return (transacoes != null && !transacoes.isEmpty()) ? ResponseEntity.ok(transacoes) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (NomeVazioException e) {
			logger.error("Nome vazio: " + e.getMessage(), e);
			throw e;
		} catch (DataInvalidaException e) {
			logger.error("Data inválida: " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Erro ao buscar transações por período e nome", e);
			throw new TransferenciaException("Exception: Erro ao buscar transações por período e nome", e);
		}
	}

	// Obter Saldo Total por Nome
	// Big O(n)
	/**
	 * Retrieves the Total Balance by Name.
	 *
	 * This method calculates the total balance for a given name by invoking the corresponding service.
	 * It handles various scenarios, such as validating the input, checking for a valid name,
	 * and handling exceptions gracefully.
	 *
	 * @param nome The name for which the total balance is to be calculated.
	 * @return ResponseEntity<Double> A response entity containing the total balance as a Double value.
	 *         - If the name is valid and the calculation is successful, it returns an OK response with the total balance.
	 *         - If the name is empty or null, it returns a Bad Request response.
	 *         - If the name is valid but no balance is found, it returns a Not Found response.
	 *         - If any exception occurs during the calculation, it returns an Internal Server Error response.
	 */
	@Operation(summary = "Retorna o saldo total por Nome registrado no Banco.", description = "Retornar o saldo total por Nome registrado no Banco.")
	@GetMapping("/saldo-total")
	public ResponseEntity<Double> calcularSaldoTotalPorNome(@RequestParam String nome) {
		try {
			if (nome == null || nome.isEmpty()) {
				logger.warn("O parâmetro 'nome' não pode ser nulo ou vazio.");
			}
			Optional<Double> saldo = Optional.of(contaService.calcularSaldoTotalPorNome(nome));
			if (!saldo.isPresent()) {
				return ResponseEntity.notFound().build();
			} else {
				double saldoTotal = saldo.get();
				return ResponseEntity.ok(saldoTotal);
			}
		} catch (IllegalArgumentException e) {
			logger.error("Argumento inválido fornecido: " + e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			logger.error("Ocorreu um erro ao calcular o saldo total por nome.", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	// "1. A sua api deve fornecer os dados de transferência de acordo com o número da conta bacária."
	// Obter o Saldo Total durante o periodo indicado.
	// Big O(n)
	/**
	 * Retrieves the Total Balance during the specified period for a given name.
	 *
	 * @param nome       The name to calculate the balance for.
	 * @param dataInicio The start date of the period in "dd/MM/yyyy" format.
	 * @param dataFim    The end date of the period in "dd/MM/yyyy" format.
	 * @return A ResponseEntity containing the total balance as a Double value if
	 *         the calculation is successful, or a ResponseEntity with an
	 *         appropriate status if there are validation errors or an error occurs
	 *         during the calculation.
	 */
	@Operation(summary = "Retorna o saldo total no periodo especificado por Nome, data de início e data de fim registrado no Banco.", description = "Retornar o saldo total no periodo especificado por Nome, data de início e data de fim registrado no Banco.")
	@GetMapping("/saldo-periodo")
	public ResponseEntity<Double> calcularSaldoPeriodoPorNome(@RequestParam String nome,
			@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") String dataInicio,
			@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") String dataFim) {
		
		if (!transferenciaService.isValidDateFormat(dataInicio) || !transferenciaService.isValidDateFormat(dataFim)) {
			logger.warn("Data inválida fornecida pelo usuário");
			return ResponseEntity.badRequest().build();
		}
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			ZonedDateTime dataInicioCompleta = LocalDate.parse(dataInicio, formatter).atStartOfDay(ZoneId.systemDefault());
			ZonedDateTime dataFimCompleta = LocalDate.parse(dataFim, formatter).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

			if (nome.isEmpty()) {
				logger.warn("O nome está vazio");
				return ResponseEntity.badRequest().build();
			}
			if(dataInicioCompleta.isAfter(dataFimCompleta)) {
				logger.warn("A Data Inicial é Posterior a Data Fim");
	            return ResponseEntity.badRequest().build();
			}
			Double saldoPeriodo = contaService.calcularSaldoPeriodoPorNome(dataInicioCompleta, dataFimCompleta, nome);

			return (saldoPeriodo != null) ? ResponseEntity.ok(saldoPeriodo) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (IllegalArgumentException e) {
			logger.error("Argumento inválido fornecido ao calcular o saldo do período para o nome: " + nome, e);
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			logger.error("Ocorreu um erro ao calcular o saldo do período para o nome: " + nome, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Sacar e salvar nas transacoes.
	// Big O(n)
	/**
	 * Withdraws funds from an account.
	 *
	 * @param idConta The ID of the account from which to withdraw funds.
	 * @param valor   The amount to be withdrawn.
	 * @return A ResponseEntity representing the result of the withdrawal operation.
	 *         - If the withdrawal is successful, it returns a response with HTTP
	 *         status 200 (OK). - If the value is invalid (less than or equal to
	 *         zero), it returns a response with HTTP status 400 (Bad Request). - If
	 *         the account is not found, it returns a response with HTTP status 404
	 *         (Not Found). - If an error occurs during the withdrawal operation, it
	 *         returns a response with HTTP status 500 (Internal Server Error).
	 */
	@Operation(summary = "Realiza o Saque por Id do Usuário.", description = "Realizar o Saque por Id do Usuário.")
	@PostMapping("/{id}/saque")
	public ResponseEntity<Void> sacar(@PathVariable("id") Long idConta, @RequestParam double valor) {
		try {
			logger.info("Iniciando operação de saque. Conta: {}, Valor: R${}", idConta, valor);
			if (valor <= 0) {
				logger.warn("Valor inválido para saque. Conta: {}, Valor: R${}", idConta, valor);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			Conta conta = contaService.obterContaPorId(idConta);
			if (conta == null) {
				logger.warn("Conta não encontrada. Conta: {}", idConta);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			contaService.sacar(idConta, valor);

			logger.info("Saque concluído com sucesso. Conta: {}, Valor: R${}", idConta, valor);

			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			logger.error("Erro ao sacar. Conta: {}, Valor: R${}", idConta, valor);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			logger.error("Erro interno ao sacar. Conta: {}, Valor: R${}", idConta, valor, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}