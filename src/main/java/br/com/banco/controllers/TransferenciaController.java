package br.com.banco.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

import br.com.banco.entities.Transferencia;
import br.com.banco.services.ContaService;
import br.com.banco.services.TransferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "TRANSFERENCIAS", description = "Endpoints Management.")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/transfers")
public class TransferenciaController {
	@Autowired
	private TransferenciaService transferenciaService;

	@Autowired
	private ContaService contaService;

	// Retorna todas as transferências relacionadas a um número de conta específico
	// 1. A sua api deve fornecer os dados de transferência de acordo com o número da conta bacária.
	/**
	 * 
	 * @param numeroConta
	 * @return
	 */
	@Operation(summary = "Retorna todas as transferências relacionadas a um número de conta específico")
	@GetMapping("/conta/{numeroConta}")
	public ResponseEntity<List<Transferencia>> getTransferenciasPorConta(
			@Parameter(description = "Número da conta", example = "12345") 
			@PathVariable Long numeroConta) {
		List<Transferencia> transferencias = transferenciaService.getTransferenciasPorConta(numeroConta);
		
		return (transferencias != null && !transferencias.isEmpty()) ? ResponseEntity.ok(transferencias) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	// Retorna todas as transferências sem nenhum filtro
	// 2. Caso não seja informado nenhum filtro, retornar todos os dados de transferência.
	// Retornar todas as transferencias existentes.
	/**
	 * 
	 * @return
	 */
	@Operation(summary = "Retorna todas as transferências sem nenhum filtro")
	@GetMapping()
	public ResponseEntity<List<Transferencia>> getAllTransferencias() {
		List<Transferencia> transferencias = transferenciaService.getAllTransferencias();
		
		return (transferencias != null && !transferencias.isEmpty()) ? ResponseEntity.ok(transferencias) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	// Retorna todas as transferências dentro de um período de tempo especificado
	// 3. Caso seja informado um período de tempo, retornar todas as transferências relacionadas à aquele período de tempo.
	/**
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	@Operation(summary = "Retorna todas as transferências dentro de um período de tempo especificado")
	@GetMapping("/periodo")
	public ResponseEntity<List<Transferencia>> getTransferenciasPorPeriodo(
	    @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
	    @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim) {

		 LocalDateTime dataInicioCompleta = dataInicio.atStartOfDay();
		 LocalDateTime dataFimCompleta = dataFim.atTime(23, 59, 59);
	    
	    List<Transferencia> transferencias = transferenciaService.getTransferenciasPorPeriodo(dataInicioCompleta, dataFimCompleta);
	    
	    return (transferencias != null && !transferencias.isEmpty()) ? ResponseEntity.ok(transferencias) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	// Retorna todas as transferências relacionadas a um operador específico
    // 4. Caso seja informado o nome do operador da transação, retornar todas as transferências relacionados à aquele operador.
	// OBTER TODAS AS TRANSFERENCIAS PELO NOME DO OPERADOR NAS TRANSFERENCIAS.
	/**
	 * 
	 * @param nomeOperador
	 * @return
	 */
    @Operation(summary = "Retorna todas as transferências relacionadas a um operador específico")
	@GetMapping("/operador")
	public ResponseEntity<List<Transferencia>> getTransferenciasPorOperador(
			@Parameter(description = "Nome do operador", example = "João") 
			@RequestParam String nomeOperador) {
		List<Transferencia> transferencias = transferenciaService.getTransferenciasPorOperador(nomeOperador);

		return (transferencias != null && !transferencias.isEmpty()) ? ResponseEntity.ok(transferencias) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	// Retorna todas as transferências com base no período de tempo e operador especificados
	// 5. Caso todos os filtros sejam informados, retornar todas as transferências com base no período de tempo informado e o nome do operador.
    // OBTER TRANSFERENCIAS APENAS COM UMA DATA INICIAL E NOME DO OPERADOR
    /**
     * 
     * @param dataInicio
     * @param dataFim
     * @param nomeOperador
     * @return
     */
    @Operation(summary = "Retorna todas as transferências com base no período de tempo e operador especificados")
    @GetMapping("/periodo-operador")
    public ResponseEntity<List<Transferencia>> getTransferenciasPorPeriodoEOperador(
            @Parameter(description = "Data de início do período", example = "dd/MM/yyyy")
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @Parameter(description = "Nome do operador", example = "Patrick")
            @RequestParam String nomeOperador) {
        LocalDateTime dataInicioCompleta = dataInicio.atStartOfDay();
        LocalDateTime dataFimCompleta = dataFim.atTime(23, 59, 59);

        List<Transferencia> transferencias = transferenciaService.getTransferenciasPorPeriodoEOperador(dataInicioCompleta, dataFimCompleta, nomeOperador);

        return (transferencias != null && !transferencias.isEmpty()) ? ResponseEntity.ok(transferencias) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

	// Retorna resultados paginados das transferências
    // Obter todas as transferencias com limite por pagina.
	@Operation(summary = "Retorna resultados paginados das transferências")
	@GetMapping("/paginadas")
	public ResponseEntity<Page<Transferencia>> getTransferenciasPaginadas(
			@Parameter(description = "Número da página", example = "0") @RequestParam int pagina,
			@Parameter(description = "Tamanho da página", example = "10") @RequestParam int tamanhoPagina) {
		Pageable pageable = PageRequest.of(pagina, tamanhoPagina);
		Page<Transferencia> transferenciasPaginadas = transferenciaService.getTransferenciasPaginadas(pageable);

		return (transferenciasPaginadas != null && !transferenciasPaginadas.isEmpty()) ? ResponseEntity.ok(transferenciasPaginadas) : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	// Cria uma nova transferência
	// TODO: METODO QUEBRADO.
	@Operation(summary = "Cria uma nova transferência")
	@PostMapping()
	public ResponseEntity<Transferencia> criarTransferencia(@RequestBody Transferencia transferencia) {
		Transferencia novaTransferencia = transferenciaService.criarTransferencia(transferencia);

		return (novaTransferencia != null) ? ResponseEntity.status(HttpStatus.CREATED).body(novaTransferencia) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
		
	// Atualiza uma transferência existente
	// TODO: VERIFICAR
	@Operation(summary = "Atualiza uma transferência existente")
	@PostMapping("/{id}")
	public ResponseEntity<Transferencia> atualizarTransferencia(
			@Parameter(description = "ID da transferência", example = "1") @PathVariable Long id,
			@RequestBody Transferencia transferencia) {
		Transferencia transferenciaAtualizada = transferenciaService.atualizarTransferencia(id, transferencia);

		return (transferenciaAtualizada != null) ? ResponseEntity.ok(transferenciaAtualizada) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	// TODO: TA REGISTRANDO NAS TRANSFERENCIAS MAS NAO ESTÁ SALVANDO O SAQUE NEGATIVO NEM IDENTIFICANDO A OPERACAO AUTOMATICAMENTE.
	@PostMapping("/{origem}/transferir/{destino}")
	public ResponseEntity<Void> transferir(
	        @PathVariable("origem") Long idContaOrigem,
	        @PathVariable("destino") Long idContaDestino,
	        @RequestParam double valor,
	        @RequestParam br.com.banco.enums.Operation tipo) {
	    contaService.transferir(idContaOrigem, idContaDestino, valor, tipo);
	    return ResponseEntity.ok().build();
	}
	
	// Pegar todas as transacoes em um periodo pelo nome.
	@GetMapping("/transacoes")
	public ResponseEntity<List<Transferencia>> buscarTransacoesPorPeriodoENome(
	        @RequestParam String nome,
	        @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
	        @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim) {
	    LocalDateTime dataInicioCompleta = dataInicio.atStartOfDay();
	    LocalDateTime dataFimCompleta = dataFim.atTime(23, 59, 59);
	    List<Transferencia> transacoes = contaService.buscarTransacoesPorPeriodoENome(dataInicioCompleta, dataFimCompleta, nome);
	    return ResponseEntity.ok(transacoes);
	}

	// Obter Saldo Total por Nome
	@GetMapping("/saldo-total")
	public ResponseEntity<Double> calcularSaldoTotalPorNome(@RequestParam String nome) {
		double saldoTotal = contaService.calcularSaldoTotalPorNome(nome);
		return ResponseEntity.ok(saldoTotal);
	}

	// Obter o Saldo Total durante o periodo indicado.
	@GetMapping("/saldo-periodo")
	public ResponseEntity<Double> calcularSaldoPeriodoPorNome(
	        @RequestParam String nome,
	        @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
	        @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim) {

	    LocalDateTime dataInicioCompleta = dataInicio.atStartOfDay();
	    LocalDateTime dataFimCompleta = dataFim.atTime(23, 59, 59);

	    double saldoPeriodo = contaService.calcularSaldoPeriodoPorNome(dataInicioCompleta, dataFimCompleta, nome);
	    return ResponseEntity.ok(saldoPeriodo);
	}

	// Sacar e salvar nas transacoes.
    @PostMapping("/{id}/saque")
    public ResponseEntity<Void> sacar(
    		@PathVariable("id") Long idConta, 
    		@RequestParam double valor) {
        contaService.sacar(idConta, valor);
        return ResponseEntity.ok().build();
    }
}