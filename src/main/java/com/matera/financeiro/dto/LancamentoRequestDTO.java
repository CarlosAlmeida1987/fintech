package com.matera.financeiro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class LancamentoRequestDTO {
    @NotBlank
    private String numeroConta;

    @NotEmpty
    @Valid
    private List<PostagemItemDTO> lancamentos;
}