package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.Seller.SellerDTO;
import com.shiftlab.crm.dto.Seller.SellerShortDTO;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;
import com.shiftlab.crm.repository.SellerRepository;
import com.shiftlab.crm.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private SellerRepository sellerRepository;
    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getMostProductiveSeller_WhenSellerExists_ShouldReturnSellerDTO() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("Лучший Продавец");
        when(transactionRepository.findMostProductiveSellerByTotalAmount(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(seller));

        SellerDTO result = analyticsService.getMostProductiveSeller("DAY");

        assertNotNull(result);
        assertEquals("Лучший Продавец", result.getName());
        assertEquals(1L, result.getId());
        verify(transactionRepository, times(1)).findMostProductiveSellerByTotalAmount(any(), any());
    }

    @Test
    void getMostProductiveSeller_WithInvalidPeriodType_ShouldThrowException() {
        String invalidPeriod = "OTHER";
        assertThrows(IllegalArgumentException.class, () -> analyticsService.getMostProductiveSeller(invalidPeriod));
    }

    @Test
    void getSellersWithTotalAmountLessThan_ShouldReturnCorrectSellers() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("Скромный продавец");
        List<Seller> sellers = Collections.singletonList(seller);
        when(sellerRepository.findSellersWithTotalAmountBelow(any(BigDecimal.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(sellers);

        List<SellerShortDTO> result = analyticsService.getSellersWithTotalAmountLessThan(
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), new BigDecimal("1000"));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Скромный продавец", result.getFirst().getName());
    }

    @Test
    void findMostProductiveTimePeriod_WhenSellerNotFound_ShouldThrowException() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> analyticsService.findMostProductiveTimePeriod(1L, 1));
    }

    @Test
    void findMostProductiveTimePeriod_WhenNoTransactions_ShouldReturnEmptyResult() {
        Seller seller = new Seller();
        seller.setId(1L);
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findBySeller(seller)).thenReturn(Collections.emptyList());

        AnalyticsService.BestPeriodResult result = analyticsService.findMostProductiveTimePeriod(1L, 1);

        assertEquals(0, result.transactionCount());
        assertNull(result.startDate());
        assertNull(result.endDate());
    }

    @Test
    void findMostProductiveTimePeriod_WithTransactions_ShouldReturnBestPeriod() {
        Seller seller = new Seller();
        seller.setId(1L);

        Transaction t1 = new Transaction();
        t1.setTransactionDate(LocalDateTime.of(2025, 10, 5, 10, 0));
        Transaction t2 = new Transaction();
        t2.setTransactionDate(LocalDateTime.of(2025, 10, 7, 12, 0));
        Transaction t3 = new Transaction();
        t3.setTransactionDate(LocalDateTime.of(2025, 10, 7, 14, 0));
        List<Transaction> transactions = List.of(t1, t2, t3);

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findBySeller(seller)).thenReturn(transactions);

        AnalyticsService.BestPeriodResult result = analyticsService.findMostProductiveTimePeriod(1L, 1);

        assertEquals(2, result.transactionCount());
        assertEquals(LocalDate.of(2025, 10, 7), result.startDate());
    }
}