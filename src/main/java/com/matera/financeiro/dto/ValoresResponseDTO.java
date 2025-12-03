package com.matera.financeiro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ValoresResponseDTO {
    private String numeroConta;
    private BigDecimal digito;
}