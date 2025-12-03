package com.matera.financeiro.service;

import com.matera.financeiro.dto.PixDepositoRequestDTO;
import com.matera.financeiro.dto.PixTransferirRequestDTO;
import com.matera.financeiro.dto.PixTransferirResponseDTO;
import com.matera.financeiro.exception.BusinessException;
import com.matera.financeiro.model.Conta;
import com.matera.financeiro.model.Postagem;
import com.matera.financeiro.model.PostagemType;
import com.matera.financeiro.repository.ContaRepository;
import com.matera.financeiro.repository.PostagemRepository;
import com.matera.financeiro.serviceImpl.PostagemServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PixServiceTest {

    private ContaRepository contaRepository;
    private PostagemRepository postagemRepository;
    private PostagemService postagemService;

    @BeforeEach
    void setup() {
        contaRepository = mock(ContaRepository.class);
        postagemRepository = mock(PostagemRepository.class);
        postagemService = new PostagemServiceImpl(contaRepository, postagemRepository);
    }

    @Test
    void shouldDepositViaPix() {
        Conta destino = Conta.builder().id(2L).number("0002").balance(new BigDecimal("100.00")).build();
        when(contaRepository.findWithLockByNumber("0002")).thenReturn(Optional.of(destino));

        PixDepositoRequestDTO req = new PixDepositoRequestDTO();
        req.setContaDestino("0002");
        req.setValor(new BigDecimal("40.00"));
        req.setChavePix("email@exemplo.com");

        var resp = postagemService.pixDeposit(req);
        assertEquals("0002", resp.getNumeroConta());
        assertEquals(new BigDecimal("140.00"), resp.getBalance());
        verify(postagemRepository, times(1)).save(any(Postagem.class));
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void shouldTransferViaPix() {
        Conta origem = Conta.builder().id(1L).number("0001").balance(new BigDecimal("100.00")).build();
        Conta destino = Conta.builder().id(2L).number("0002").balance(new BigDecimal("50.00")).build();
        when(contaRepository.findWithLockByNumber("0001")).thenReturn(Optional.of(origem));
        when(contaRepository.findWithLockByNumber("0002")).thenReturn(Optional.of(destino));

        PixTransferirRequestDTO req = new PixTransferirRequestDTO();
        req.setContaOrigem("0001");
        req.setContaDestino("0002");
        req.setValor(new BigDecimal("40.00"));
        req.setChavePix("cpf:000.000.000-00");

        PixTransferirResponseDTO resp = postagemService.pixTransfer(req);
        assertEquals("0001", resp.getContaOrigem());
        assertEquals(new BigDecimal("60.00"), resp.getSaldoOrigem());
        assertEquals("0002", resp.getContaDestino());
        assertEquals(new BigDecimal("90.00"), resp.getSaldoDestino());
        verify(postagemRepository, times(2)).save(any(Postagem.class));
        verify(contaRepository, times(1)).save(origem);
        verify(contaRepository, times(1)).save(destino);
    }

    @Test
    void shouldFailTransferWhenInsufficientBalance() {
        Conta origem = Conta.builder().id(1L).number("0001").balance(new BigDecimal("30.00")).build();
        Conta destino = Conta.builder().id(2L).number("0002").balance(new BigDecimal("50.00")).build();
        when(contaRepository.findWithLockByNumber("0001")).thenReturn(Optional.of(origem));
        when(contaRepository.findWithLockByNumber("0002")).thenReturn(Optional.of(destino));

        PixTransferirRequestDTO req = new PixTransferirRequestDTO();
        req.setContaOrigem("0001");
        req.setContaDestino("0002");
        req.setValor(new BigDecimal("40.00"));
        req.setChavePix("telefone:+550000000000");

        assertThrows(BusinessException.class, () -> postagemService.pixTransfer(req));
        verify(postagemRepository, never()).save(any());
        verify(contaRepository, never()).save(any());
    }
}
