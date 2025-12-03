package com.matera.financeiro.service;

import com.matera.financeiro.dto.BalanceResponseDTO;
import com.matera.financeiro.dto.LancamentoRequestDTO;
import com.matera.financeiro.dto.PixDepositoRequestDTO;
import com.matera.financeiro.dto.PixTransferirRequestDTO;
import com.matera.financeiro.dto.PixTransferirResponseDTO;
import com.matera.financeiro.dto.PostagemRequestDTO;
import com.matera.financeiro.dto.ValoresResponseDTO;

import jakarta.validation.Valid;


public interface PostagemService {

    
    public BalanceResponseDTO processPostings(PostagemRequestDTO request);

    public BalanceResponseDTO getBalance(String numeroConta);
    
	public ValoresResponseDTO procesLancamentos(@Valid LancamentoRequestDTO request);

    public BalanceResponseDTO pixDeposit(@Valid PixDepositoRequestDTO request);

    public PixTransferirResponseDTO pixTransfer(@Valid PixTransferirRequestDTO request);
}
