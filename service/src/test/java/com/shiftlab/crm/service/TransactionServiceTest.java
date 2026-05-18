package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.transaction.TransactionDTO;
import com.shiftlab.crm.dto.TransactionRequest;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.fixture.TestDataFactory;
import com.shiftlab.crm.model.PaymentType;
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
        seller = TestDataFactory.seller("Тест Продавец");
        transaction = TestDataFactory.transaction(seller);
    }

    @Test
    void createTransaction_WhenSellerExists_ShouldSaveTransaction() {
        TransactionRequest request = TestDataFactory.transactionRequest(new BigDecimal("123.45"), PaymentType.CASH);
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = transactionService.createTransaction(1L, request);

        assertEquals(1L, result.getSellerId());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertNotNull(captor.getValue().getTransactionDate());
        assertEquals(seller, captor.getValue().getSeller());
    }

    @Test
    void createTransaction_WhenSellerNotFound_ShouldThrowException() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction(1L, new TransactionRequest()));
    }

    @Test
    void getTransactionById_WhenFound_ShouldReturnDTO() {
        when(transactionRepository.findById(TestDataFactory.DEFAULT_TRANSACTION_ID)).thenReturn(Optional.of(transaction));

        TransactionDTO result = transactionService.getTransactionById(TestDataFactory.DEFAULT_TRANSACTION_ID);

        assertNotNull(result);
        assertEquals(TestDataFactory.DEFAULT_TRANSACTION_ID, result.getId());
        assertEquals(TestDataFactory.DEFAULT_AMOUNT, result.getAmount());
    }

    @Test
    void getTransactionById_WhenNotFound_ShouldThrowException() {
        when(transactionRepository.findById(TestDataFactory.DEFAULT_TRANSACTION_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.getTransactionById(TestDataFactory.DEFAULT_TRANSACTION_ID));
    }

    @Test
    void updateTransaction_WhenFound_ShouldUpdateAndReturnDTO() {
        TransactionRequest request = TestDataFactory.transactionRequest(new BigDecimal("500.00"), PaymentType.CARD);
        when(transactionRepository.findById(TestDataFactory.DEFAULT_TRANSACTION_ID)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        TransactionDTO result = transactionService.updateTransaction(TestDataFactory.DEFAULT_TRANSACTION_ID, request);

        assertEquals(new BigDecimal("500.00"), result.getAmount());
        assertEquals(PaymentType.CARD, result.getPaymentType());
    }

    @Test
    void deleteTransaction_WhenFound_ShouldCallDelete() {
        when(transactionRepository.findById(TestDataFactory.DEFAULT_TRANSACTION_ID)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(TestDataFactory.DEFAULT_TRANSACTION_ID);

        verify(transactionRepository, times(1)).delete(transaction);
    }

    @Test
    void deleteTransaction_WhenNotFound_ShouldThrowException() {
        when(transactionRepository.findById(TestDataFactory.DEFAULT_TRANSACTION_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.deleteTransaction(TestDataFactory.DEFAULT_TRANSACTION_ID));
    }

    @Test
    void getTransactionsBySellerId_WhenSellerNotFound_ShouldThrowException() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.getTransactionsBySellerId(1L, 0, 10));
    }
}
