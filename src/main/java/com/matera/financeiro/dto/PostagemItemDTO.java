package com.matera.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostagemItemDTO {
    @NotNull
    @Pattern(regexp = "DEBITO|CREDITO", message = "tipo deve ser DEBITO or CREDITO")
    private String tipo;

    @NotNull
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private BigDecimal quantia;
}