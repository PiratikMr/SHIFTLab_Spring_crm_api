package com.shiftlab.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiftlab.crm.dto.transaction.TransactionDTO;
import com.shiftlab.crm.dto.TransactionRequest;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.fixture.TestDataFactory;
import com.shiftlab.crm.model.PaymentType;
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
import java.util.List;

import static com.shiftlab.crm.controller.ApiPaths.BASE;
import static com.shiftlab.crm.controller.ApiPaths.SELLER_SUB;
import static com.shiftlab.crm.controller.ApiPaths.TRANSACTIONS;
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

    @Test
    void getTransactions_ShouldReturnPagedList_Success() throws Exception {
        TransactionDTO dto = TestDataFactory.transactionDTO(1L, 1L, new BigDecimal("100.50"), PaymentType.CASH);
        Page<TransactionDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        when(transactionService.getTransactions(0, 10)).thenReturn(page);

        mockMvc.perform(get(BASE + TRANSACTIONS)
                        .param("page", "0")
                        .param("perPage", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(100.50))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getTransactionById_ShouldReturnTransactionDTO_Success() throws Exception {
        TransactionDTO dto = TestDataFactory.transactionDTO(1L, 1L, new BigDecimal("500.00"), PaymentType.CARD);
        when(transactionService.getTransactionById(1L)).thenReturn(dto);

        mockMvc.perform(get(BASE + TRANSACTIONS + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.paymentType").value("CARD"));
    }

    @Test
    void createTransaction_ShouldReturnCreatedTransaction_Success() throws Exception {
        Long sellerId = 1L;
        BigDecimal amount = new BigDecimal("123.45");
        TransactionRequest request = TestDataFactory.transactionRequest(amount, PaymentType.CASH);
        TransactionDTO createdDto = TestDataFactory.transactionDTO(10L, sellerId, amount, PaymentType.CASH);
        when(transactionService.createTransaction(eq(sellerId), any(TransactionRequest.class))).thenReturn(createdDto);

        mockMvc.perform(post(BASE + TRANSACTIONS + SELLER_SUB + "/" + sellerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(123.45))
                .andExpect(jsonPath("$.paymentType").value("CASH"));

        verify(transactionService, times(1)).createTransaction(eq(sellerId), any(TransactionRequest.class));
    }

    @Test
    void getTransactionsBySellerId_ShouldReturnList_Success() throws Exception {
        Long sellerId = 2L;
        TransactionDTO dto = TestDataFactory.transactionDTO(1L, sellerId, new BigDecimal("250.00"), PaymentType.CARD);
        Page<TransactionDTO> mockPage = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1L);
        when(transactionService.getTransactionsBySellerId(eq(sellerId), anyInt(), anyInt())).thenReturn(mockPage);

        mockMvc.perform(get(BASE + TRANSACTIONS + SELLER_SUB + "/" + sellerId)
                        .param("page", "0")
                        .param("perPage", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(250.00))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void createTransaction_ShouldReturnBadRequest_WhenAmountIsNegative() throws Exception {
        Long sellerId = 1L;
        TransactionRequest invalidRequest = TestDataFactory.transactionRequest(new BigDecimal("-10.00"), PaymentType.CARD);

        mockMvc.perform(post(BASE + TRANSACTIONS + SELLER_SUB + "/" + sellerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Сумма транзакции не может быть отрицательной")));
    }

    @Test
    void createTransaction_ShouldReturnBadRequest_WhenPaymentTypeIsInvalid() throws Exception {
        mockMvc.perform(post(BASE + TRANSACTIONS + SELLER_SUB + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100, \"paymentType\": \"BITCOIN\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTransaction_ShouldReturnNoContent_Success() throws Exception {
        doNothing().when(transactionService).deleteTransaction(1L);

        mockMvc.perform(delete(BASE + TRANSACTIONS + "/1"))
                .andExpect(status().isNoContent());

        verify(transactionService, times(1)).deleteTransaction(1L);
    }

    @Test
    void deleteTransaction_ShouldReturnNotFound_WhenMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Транзакция не найдена"))
                .when(transactionService).deleteTransaction(999L);

        mockMvc.perform(delete(BASE + TRANSACTIONS + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Транзакция не найдена"));
    }
}
