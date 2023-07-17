package br.com.banco.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.banco.entities.Conta;
import br.com.banco.exceptions.InvalidAccountIdException;
import br.com.banco.exceptions.InvalidWithdrawalAmountException;
import br.com.banco.services.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CONTAS", description = "Endpoints Management.")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/contas")
public class ContaController {
	
	private static final Logger logger = LoggerFactory.getLogger(TransferenciaController.class);
	
    private final ContaService contaService;
    
    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    // Criar Conta
    /**
    * Creates a new account.
    *
    * @param requestBody The request body containing the account details.
    * @return ResponseEntity containing the newly created account details if successful, or an error response.
    */
    @Operation(summary = "Cria uma Nova transferencia.", description = "Criar uma Nova transferencia.")
    @PostMapping
    public ResponseEntity<Map<String, Object>> criarConta(@RequestBody Map<String, Object> requestBody) {
        try {
            String nome = (String) requestBody.get("nome");
            if (nome == null || nome.isEmpty()) {
                logger.warn("O parâmetro 'nome' é obrigatório");
            }
            if (contaService.hasContaByName(nome)) {
            	logger.warn("A conta com o nome '" + nome + "' já existe");
            }
            Conta novaConta = contaService.criarConta(nome);
            Map<String, Object> response = new HashMap<>();
            response.put("id", novaConta.getId());
            response.put("nome", novaConta.getNome());
            response.put("dataDeCriacao", novaConta.getDataDeCriacao());
            response.put("saldo", novaConta.getSaldo());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Erro ao criar conta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(contaService.createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro interno ao criar conta", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(contaService.createErrorResponse("Erro interno ao criar conta"));
        }
    }

    // Depositar valor por id.
    /**
     * Deposits an amount by ID.
     *
     * @param id    The ID of the account to deposit into.
     * @param valor The amount to deposit.
     * @return ResponseEntity indicating the status of the deposit operation.
     */
    @Operation(summary = "Deposita um valor por Id.", description = "Depositar um valor por Id.")
    @PostMapping("/{id}/depositar")
    public ResponseEntity<Void> depositar(@PathVariable Long id, @RequestParam double valor) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("O parâmetro 'id' é obrigatório");
            }
            if (!contaService.hasConta(id)) {
                throw new IllegalArgumentException("A conta com o ID '" + id + "' não existe");
            }
            contaService.depositar(id, valor);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Erro ao depositar valor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Erro interno ao depositar valor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Sacar por id.
    /**
     * Withdraws an amount by ID.
     *
     * @param id    The ID of the account to withdraw from.
     * @param valor The amount to withdraw.
     * @return ResponseEntity indicating the status of the withdrawal operation.
     */
    @Operation(summary = "Saca uma quantia por Id.", description = "Sacar uma quantida por Id.")
    @PostMapping("/{id}/sacar")
    public ResponseEntity<Void> sacar(@PathVariable Long id, @RequestParam double valor) {
        try {
            if (id <= 0) {
                throw new InvalidAccountIdException("O ID da conta deve ser um número positivo.");
            }
            if (valor <= 0) {
                throw new InvalidWithdrawalAmountException("O valor do saque deve ser um número positivo.");
            }
            if (!contaService.hasConta(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            contaService.sacar(id, valor);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Parâmetros inválidos: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
        	logger.error("Erro ao sacar da conta com ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Achar por Id
    /**
     * Retrieves an account by ID.
     *
     * @param id The ID of the account to retrieve.
     * @return ResponseEntity containing the account details if found, or an appropriate error response.
     */
    @Operation(summary = "Obtem uma conta por Id.", description = "Obter uma conta por Id.")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obterContaPorId(@PathVariable Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("O ID da conta não pode ser nulo.");
            }
            if (!contaService.hasConta(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Conta conta = contaService.obterContaPorId(id);
            if (conta == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            Map<String, Object> response = new HashMap<>();
            response.put("id", conta.getId());
            response.put("nome", conta.getNome());
            response.put("saldo", conta.getSaldo());

            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Argumento inválido fornecido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Ocorreu um erro ao obter a conta por ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Transferencia
    /**
     * Performs a transfer between two accounts.
     *
     * @param idContaOrigem The ID of the source account.
     * @param idContaDestino The ID of the destination account.
     * @param valor The transfer amount.
     * @param tipo The operation type.
     * @return A ResponseEntity indicating the status of the transfer operation.
     *         Returns HTTP 200 (OK) if the transfer is successful.
     *         Returns HTTP 400 (Bad Request) if there is a validation error in the parameters.
     *         Returns HTTP 500 (Internal Server Error) if an unexpected error occurs during the transfer.
     */
    @Operation(summary = "Realiza transferencia entre duas contas.", description = "Realizar transferencia entre duas contas.")
    @PostMapping("/{origem}/transferir/{destino}")
    public ResponseEntity<Void> transferir(
            @PathVariable("origem") Long idContaOrigem,
            @PathVariable("destino") Long idContaDestino,
            @RequestParam double valor,
            @RequestParam br.com.banco.enums.Operation tipo) {
        try {
        	contaService.validarParametros(idContaOrigem, idContaDestino, valor, tipo);
            contaService.transferir(idContaOrigem, idContaDestino, valor, tipo);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.error("Erro de validação nos parâmetros da transferência: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Ocorreu um erro inesperado durante a transferência: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Atualizar saldo sem salvar nas transferencias
    /**
     * Updates the balance of an account without saving the transaction details.
     *
     * @param id    The ID of the account to update the balance.
     * @param valor The amount to deposit.
     * @return The response entity indicating the status of the operation.
     */
    @Operation(summary = "Atualiza o saldo por id.", description = "Atualizar o saldo por id.")
    @PutMapping("/{id}/saldo")
	public ResponseEntity<Void> atualizarSaldo(@PathVariable Long id, @RequestParam double valor) {
		try {
			if (id == null) {
				throw new IllegalArgumentException("O ID da conta não pode ser nulo.");
			}
			if (!contaService.hasConta(id)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			if (valor <= 0) {
				throw new IllegalArgumentException("O valor deve ser maior que zero.");
			}
			contaService.depositar(id, valor);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			logger.error("Erro ao atualizar saldo: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			logger.error("Erro inesperado ao atualizar saldo.", e);
			return ResponseEntity.status(500).build();
		}
	}
}