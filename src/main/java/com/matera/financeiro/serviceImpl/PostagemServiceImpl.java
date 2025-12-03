package com.matera.financeiro.serviceImpl;

import com.matera.financeiro.dto.BalanceResponseDTO;
import com.matera.financeiro.dto.LancamentoRequestDTO;
import com.matera.financeiro.dto.PostagemItemDTO;
import com.matera.financeiro.dto.PostagemRequestDTO;
import com.matera.financeiro.dto.ValoresResponseDTO;
import com.matera.financeiro.dto.PixTransferirRequestDTO;
import com.matera.financeiro.dto.PixDepositoRequestDTO;
import com.matera.financeiro.dto.PixTransferirResponseDTO;
import com.matera.financeiro.exception.BusinessException;
import com.matera.financeiro.exception.NotFoundException;
import com.matera.financeiro.model.Conta;
import com.matera.financeiro.model.Postagem;
import com.matera.financeiro.model.PostagemType;
import com.matera.financeiro.repository.ContaRepository;
import com.matera.financeiro.repository.PostagemRepository;
import com.matera.financeiro.service.PostagemService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PostagemServiceImpl implements PostagemService {

    private final ContaRepository contaRepository;
    private final PostagemRepository postagemRepository;

    public PostagemServiceImpl(ContaRepository contaRepository, PostagemRepository postagemRepository) {
        this.contaRepository = contaRepository;
        this.postagemRepository = postagemRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BalanceResponseDTO processPostings(PostagemRequestDTO request) {
        Conta conta = contaRepository.findWithLockByNumber(request.getNumeroConta())
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

        BigDecimal newBalance = conta.getBalance();
        for (PostagemItemDTO item : request.getPostagens()) {
            PostagemType type = PostagemType.valueOf(item.getTipo());
            BigDecimal amt = item.getQuantia();
            if (type == PostagemType.DEBITO) {
                if (newBalance.compareTo(amt) < 0) {
                    throw new BusinessException("Saldo insuficiente para débito: " + amt);
                }
                newBalance = newBalance.subtract(amt);
            } else {
                newBalance = newBalance.add(amt);
            }
            Postagem postagem = Postagem.builder()
                    .conta(conta)
                    .type(type)
                    .valor(amt)
                    .createdAt(Instant.now())
                    .build();
            postagemRepository.save(postagem);
        }
        conta.setBalance(newBalance);
        contaRepository.save(conta);
        return new BalanceResponseDTO(conta.getNumber(), conta.getBalance());
    }

    @Transactional(readOnly = true)
    public BalanceResponseDTO getBalance(String numeroConta) {
        Conta account = contaRepository.findByNumber(numeroConta)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));
        return new BalanceResponseDTO(account.getNumber(), account.getBalance());
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
	public ValoresResponseDTO procesLancamentos(@Valid LancamentoRequestDTO request) {
		 Conta conta = contaRepository.findWithLockByNumber(request.getNumeroConta())
	                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

	        BigDecimal newBalance = conta.getBalance();
	        for (PostagemItemDTO item : request.getLancamentos()) {
	            PostagemType type = PostagemType.valueOf(item.getTipo());
	            BigDecimal amt = item.getQuantia();
	            if (type == PostagemType.DEBITO) {
	                if (newBalance.compareTo(amt) < 0) {
	                    throw new BusinessException("Saldo insuficiente para débito: " + amt);
	                }
	                newBalance = newBalance.subtract(amt);
	            } else {
	                newBalance = newBalance.add(amt);
	            }
	            Postagem postagem = Postagem.builder()
	                    .conta(conta)
	                    .type(type)
	                    .valor(amt)
	                    .createdAt(Instant.now())
	                    .build();
	            postagemRepository.save(postagem);
	        }
	        conta.setBalance(newBalance);
	        contaRepository.save(conta);
	        return new ValoresResponseDTO(conta.getNumber(), conta.getBalance());
	}

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BalanceResponseDTO pixDeposit(@Valid PixDepositoRequestDTO request) {
        Conta conta = contaRepository.findWithLockByNumber(request.getContaDestino())
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

        BigDecimal novoSaldo = conta.getBalance().add(request.getValor());

        Postagem postagem = Postagem.builder()
                .conta(conta)
                .type(PostagemType.CREDITO)
                .valor(request.getValor())
                .createdAt(Instant.now())
                .build();
        postagemRepository.save(postagem);

        conta.setBalance(novoSaldo);
        contaRepository.save(conta);
        return new BalanceResponseDTO(conta.getNumber(), conta.getBalance());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PixTransferirResponseDTO pixTransfer(@Valid PixTransferirRequestDTO request) {
        String a = request.getContaOrigem();
        String b = request.getContaDestino();
        String first = a.compareTo(b) <= 0 ? a : b;
        String second = a.compareTo(b) <= 0 ? b : a;

        Conta firstConta = contaRepository.findWithLockByNumber(first)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));
        Conta secondConta = contaRepository.findWithLockByNumber(second)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

        Conta origem = firstConta.getNumber().equals(a) ? firstConta : secondConta;
        Conta destino = firstConta.getNumber().equals(a) ? secondConta : firstConta;

        BigDecimal valor = request.getValor();
        BigDecimal saldoOrigem = origem.getBalance();
        if (saldoOrigem.compareTo(valor) < 0) {
            throw new BusinessException("Saldo insuficiente para transferir via PIX: " + valor);
        }

        origem.setBalance(saldoOrigem.subtract(valor));
        destino.setBalance(destino.getBalance().add(valor));

        Postagem debitoOrigem = Postagem.builder()
                .conta(origem)
                .type(PostagemType.DEBITO)
                .valor(valor)
                .createdAt(Instant.now())
                .build();
        Postagem creditoDestino = Postagem.builder()
                .conta(destino)
                .type(PostagemType.CREDITO)
                .valor(valor)
                .createdAt(Instant.now())
                .build();

        postagemRepository.save(debitoOrigem);
        postagemRepository.save(creditoDestino);
        contaRepository.save(origem);
        contaRepository.save(destino);

        return new PixTransferirResponseDTO(
                origem.getNumber(), origem.getBalance(),
                destino.getNumber(), destino.getBalance()
        );
    }
}
