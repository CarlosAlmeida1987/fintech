package com.matera.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceResponseDTO {
    private String numeroConta;
    private BigDecimal balance;
}