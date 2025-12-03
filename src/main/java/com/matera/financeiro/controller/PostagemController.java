package com.matera.financeiro.controller;

import com.matera.financeiro.dto.BalanceResponseDTO;
import com.matera.financeiro.dto.LancamentoRequestDTO;
import com.matera.financeiro.dto.PostagemRequestDTO;
import com.matera.financeiro.dto.ValoresResponseDTO;
import com.matera.financeiro.dto.PixDepositoRequestDTO;
import com.matera.financeiro.dto.PixTransferirRequestDTO;
import com.matera.financeiro.dto.PixTransferirResponseDTO;
import com.matera.financeiro.service.PostagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lançamentos", description = "API para realizar lançamentos de débito e crédito e consultar saldo")
@RestController
@RequestMapping(path = "/api/contas", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostagemController {

    private final PostagemService postagemService;

    public PostagemController(PostagemService postagemService) {
        this.postagemService = postagemService;
    }

    @Operation(summary = "Realizar lançamentos", description = "Realiza lançamentos de débito e crédito em uma conta. Permite múltiplos lançamentos na mesma requisição.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lançamentos realizados com sucesso",
                            content = @Content(schema = @Schema(implementation = BalanceResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
                    @ApiResponse(responseCode = "422", description = "Erro de negócio (ex: saldo insuficiente)")
            })
    @PostMapping(path = "/lancamentos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BalanceResponseDTO> post(@Valid @RequestBody PostagemRequestDTO request) {
        BalanceResponseDTO response = postagemService.processPostings(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obter saldo", description = "Retorna o saldo atual de uma conta",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Saldo retornado",
                            content = @Content(schema = @Schema(implementation = BalanceResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada")
            })
    @GetMapping(path = "/{numeroConta}/balance")
    public ResponseEntity<BalanceResponseDTO> balance(@PathVariable String numeroConta) {
        BalanceResponseDTO response = postagemService.getBalance(numeroConta);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Realizar lançamentos", description = "Realiza lançamentos de débito e crédito em uma conta. Permite múltiplos lançamentos na mesma requisição.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lançamentos realizados com sucesso",
                            content = @Content(schema = @Schema(implementation = BalanceResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
                    @ApiResponse(responseCode = "422", description = "Erro de negócio (ex: saldo insuficiente)")
            })
    @PostMapping(path = "/pagamentos", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ValoresResponseDTO> caixa(@Valid @RequestBody LancamentoRequestDTO request) {
        ValoresResponseDTO response = postagemService.procesLancamentos(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Depositar via PIX", description = "Credita valor na conta destino via PIX",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Depósito realizado",
                            content = @Content(schema = @Schema(implementation = BalanceResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada")
            })
    @PostMapping(path = "/pix/depositar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BalanceResponseDTO> pixDepositar(@Valid @RequestBody PixDepositoRequestDTO request) {
        BalanceResponseDTO response = postagemService.pixDeposit(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Transferir via PIX", description = "Transfere valor entre contas via PIX",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transferência realizada",
                            content = @Content(schema = @Schema(implementation = PixTransferirResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação"),
                    @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
                    @ApiResponse(responseCode = "422", description = "Saldo insuficiente")
            })
    @PostMapping(path = "/pix/transferir", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PixTransferirResponseDTO> pixTransferir(@Valid @RequestBody PixTransferirRequestDTO request) {
        PixTransferirResponseDTO response = postagemService.pixTransfer(request);
        return ResponseEntity.ok(response);
    }
}
