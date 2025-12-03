package com.matera.financeiro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matera.financeiro.dto.PostagemItemDTO;
import com.matera.financeiro.dto.PostagemRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPostAndGetBalance() throws Exception {
        PostagemItemDTO debito = new PostagemItemDTO();
        debito.setTipo("DEBITO");
        debito.setQuantia(new BigDecimal("100.00"));
        PostagemItemDTO credito = new PostagemItemDTO();
        credito.setTipo("CREDITO");
        credito.setQuantia(new BigDecimal("50.00"));
        PostagemRequestDTO req = new PostagemRequestDTO();
        req.setNumeroConta("0001");
        req.setPostagens(List.of(debito, credito));

        mockMvc.perform(post("/api/contas/lancamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("0001"))
                .andExpect(jsonPath("$.balance").value(950.00));

        mockMvc.perform(get("/api/contas/0001/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(950.00));
    }
}