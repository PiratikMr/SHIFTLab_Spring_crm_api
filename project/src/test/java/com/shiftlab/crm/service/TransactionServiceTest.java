package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.Transaction.TransactionDTO;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;
import com.shiftlab.crm.repository.SellerRepository;
import com.shiftlab.crm.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private SellerRepository sellerRepository;
    @InjectMocks
    private TransactionService transactionService;

    private Seller seller;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        seller = new Seller();
        seller.setId(1L);
        seller.setName("Тест Продавец");

        transaction = new Transaction();
        transaction.setId(100L);
        transaction.setAmount(new BigDecimal("123.45"));
        transaction.setSeller(seller);
    }

    @Test
    void createTransaction_WhenSellerExists_ShouldSaveTransaction() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = transactionService.createTransaction(1L, new Transaction());

        assertEquals(1L, result.getSeller_id());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertNotNull(captor.getValue().getTransactionDate());
        assertEquals(seller, captor.getValue().getSeller());
    }

    @Test
    void createTransaction_WhenSellerNotFound_ShouldThrowException() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction(1L, new Transaction()));
    }

    @Test
    void getTransactionById_WhenFound_ShouldReturnDTO() {
        when(transactionRepository.findById(100L)).thenReturn(Optional.of(transaction));

        TransactionDTO result = transactionService.getTransactionById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(new BigDecimal("123.45"), result.getAmount());
    }

    @Test
    void getTransactionById_WhenNotFound_ShouldThrowException() {
        when(transactionRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(100L));
    }

    @Test
    void updateTransaction_WhenFound_ShouldUpdateAndReturnDTO() {
        Transaction details = new Transaction();
        details.setAmount(new BigDecimal("500.00"));
        details.setPaymentType(Transaction.PaymentType.CARD);

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        TransactionDTO result = transactionService.updateTransaction(100L, details);

        assertEquals(new BigDecimal("500.00"), result.getAmount());
        assertEquals(Transaction.PaymentType.CARD, result.getPaymentType());
    }
}