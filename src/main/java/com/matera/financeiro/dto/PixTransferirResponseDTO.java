package com.matera.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PixTransferirResponseDTO {
    private String contaOrigem;
    private BigDecimal saldoOrigem;
    private String contaDestino;
    private BigDecimal saldoDestino;
}
