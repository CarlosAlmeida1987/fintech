package com.matera.financeiro.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Postagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Conta conta;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostagemType type; // DEBITO or CREDITO

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private Instant createdAt;
}