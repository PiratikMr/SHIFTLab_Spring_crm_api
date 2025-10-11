package com.shiftlab.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiftlab.crm.dto.Transaction.TransactionDTO;
import com.shiftlab.crm.dto.Transaction.TransactionShortDTO;
import com.shiftlab.crm.model.Transaction;
import com.shiftlab.crm.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private final String BASE_URL = "/api/transactions";


    @Test
    void getTransactions_ShouldReturnPagedList_Success() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        TransactionDTO dto = new TransactionDTO();
        dto.setId(1L);
        dto.setAmount(new BigDecimal("100.50"));
        dto.setTransactionDate(now);

        Page<TransactionDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        when(transactionService.getTransactions(0, 10)).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("perPage", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(100.50))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionById_ShouldReturnTransactionDTO_Success() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        TransactionDTO dto = new TransactionDTO();
        dto.setId(1L);
        dto.setAmount(new BigDecimal("500.00"));
        dto.setTransactionDate(now);
        dto.setSeller_id(1L);
        dto.setPaymentType(Transaction.PaymentType.CARD);

        when(transactionService.getTransactionById(1L)).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.paymentType").value("CARD"));
    }

    @Test
    void createTransaction_ShouldReturnCreatedTransaction_Success() throws Exception {
        Long sellerId = 1L;
        BigDecimal amount = new BigDecimal("123.45");

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(amount);
        newTransaction.setPaymentType(Transaction.PaymentType.CASH);

        TransactionDTO createdDto = new TransactionDTO();
        createdDto.setId(10L);
        createdDto.setAmount(amount);
        createdDto.setTransactionDate(LocalDateTime.now());
        createdDto.setSeller_id(sellerId);
        createdDto.setPaymentType(Transaction.PaymentType.CASH);

        when(transactionService.createTransaction(eq(sellerId), any(Transaction.class))).thenReturn(createdDto);

        mockMvc.perform(post(BASE_URL + "/seller/" + sellerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTransaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(123.45))
                .andExpect(jsonPath("$.paymentType").value("CASH"));

        verify(transactionService, times(1)).createTransaction(eq(sellerId), any(Transaction.class));
    }

    @Test
    void getTransactionsBySellerId_ShouldReturnList_Success() throws Exception {
        Long sellerId = 2L;
        int page = 0;
        int perPage = 10;
        LocalDateTime now = LocalDateTime.now();

        TransactionDTO shortDto = new TransactionDTO();
        shortDto.setId(1L);
        shortDto.setAmount(new BigDecimal("250.00"));
        shortDto.setTransactionDate(now);

        Page<TransactionDTO> mockPage = new PageImpl<>(
                List.of(shortDto),
                PageRequest.of(page, perPage),
                1L
        );

        when(transactionService.getTransactionsBySellerId(eq(sellerId), anyInt(), anyInt()))
                .thenReturn(mockPage);

        mockMvc.perform(get(BASE_URL + "/seller/" + sellerId)
                        .param("page", String.valueOf(page))
                        .param("perPage", String.valueOf(perPage))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.content[0].amount").value(250.00))

                .andExpect(jsonPath("$.content.length()").value(1))

                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(page));
    }


    @Test
    void createTransaction_ShouldReturnBadRequest_WhenAmountIsNegative() throws Exception {
        Long sellerId = 1L;
        Transaction invalidTransaction = new Transaction();
        invalidTransaction.setAmount(new BigDecimal("-10.00"));
        invalidTransaction.setPaymentType(Transaction.PaymentType.CARD);

        mockMvc.perform(post(BASE_URL + "/seller/" + sellerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTransaction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Сумма транзакции не может быть отрицательной")));
    }
}