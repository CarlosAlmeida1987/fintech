package com.matera.financeiro.service;

import com.matera.financeiro.dto.PostagemItemDTO;
import com.matera.financeiro.dto.PostagemRequestDTO;
import com.matera.financeiro.exception.BusinessException;
import com.matera.financeiro.model.Conta;
import com.matera.financeiro.model.Postagem;
import com.matera.financeiro.model.PostagemType;
import com.matera.financeiro.repository.ContaRepository;
import com.matera.financeiro.repository.PostagemRepository;
import com.matera.financeiro.serviceImpl.PostagemServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostagemServiceTest {

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
    void shouldProcessDebitAndCredit() {
        Conta acc = Conta.builder().id(1L).number("0001").balance(new BigDecimal("100.00")).build();
        when(contaRepository.findWithLockByNumber("0001")).thenReturn(Optional.of(acc));

        PostagemItemDTO debito = new PostagemItemDTO();
        debito.setTipo("DEBITO");
        debito.setQuantia(new BigDecimal("30.00"));
        PostagemItemDTO credito = new PostagemItemDTO();
        credito.setTipo("CREDITO");
        credito.setQuantia(new BigDecimal("10.00"));

        PostagemRequestDTO req = new PostagemRequestDTO();
        req.setNumeroConta("0001");
        req.setPostagens(List.of(debito, credito));

        var resp = postagemService.processPostings(req);
        assertEquals("0001", resp.getNumeroConta());
        assertEquals(new BigDecimal("80.00"), resp.getBalance());
        verify(postagemRepository, times(2)).save(any(Postagem.class));
        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    void shouldThrowWhenInsufficientFunds() {
        Conta acc = Conta.builder().id(1L).number("0001").balance(new BigDecimal("20.00")).build();
        when(contaRepository.findWithLockByNumber("0001")).thenReturn(Optional.of(acc));

        PostagemItemDTO debito = new PostagemItemDTO();
        debito.setTipo("DEBITO");
        debito.setQuantia(new BigDecimal("30.00"));
        PostagemRequestDTO req = new PostagemRequestDTO();
        req.setNumeroConta("0001");
        req.setPostagens(List.of(debito));

        assertThrows(BusinessException.class, () -> postagemService.processPostings(req));
        verify(postagemRepository, never()).save(any());
        verify(contaRepository, never()).save(any());
    }
}