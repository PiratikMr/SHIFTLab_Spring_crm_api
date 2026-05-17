package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.Seller.SellerDTO;
import com.shiftlab.crm.dto.Seller.SellerShortDTO;
import com.shiftlab.crm.exception.ResourceNotFoundException;
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

        assertThrows(ResourceNotFoundException.class, () -> analyticsService.findMostProductiveTimePeriod(1L, 1));
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

    @Test
    void findMostProductiveTimePeriod_WhenWindowLargerThanData_ShouldReturnAllTransactions() {
        Seller seller = new Seller();
        seller.setId(1L);

        Transaction t1 = new Transaction();
        t1.setTransactionDate(LocalDateTime.of(2025, 10, 1, 10, 0));
        Transaction t2 = new Transaction();
        t2.setTransactionDate(LocalDateTime.of(2025, 10, 3, 12, 0));
        List<Transaction> transactions = List.of(t1, t2);

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findBySeller(seller)).thenReturn(transactions);

        // Окно 5 дней > 2 активных дней с транзакциями → возвращаем всё
        AnalyticsService.BestPeriodResult result = analyticsService.findMostProductiveTimePeriod(1L, 5);

        assertEquals(2, result.transactionCount());
        assertEquals(LocalDate.of(2025, 10, 1), result.startDate());
        assertEquals(LocalDate.of(2025, 10, 4), result.endDate()); // last + 1 day
    }

    @Test
    void getMostProductiveSeller_ForMonth_ShouldUseStartOfDay() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("Продавец Месяца");

        when(transactionRepository.findMostProductiveSellerByTotalAmount(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime startDate = invocation.getArgument(0);
                    // Проверяем что startDate имеет нулевое время (00:00:00)
                    assertEquals(0, startDate.getHour(), "startDate должен начинаться с 00:00");
                    assertEquals(0, startDate.getMinute());
                    assertEquals(0, startDate.getSecond());
                    assertEquals(1, startDate.getDayOfMonth(), "MONTH должен начинаться с 1-го числа");
                    return Optional.of(seller);
                });

        SellerDTO result = analyticsService.getMostProductiveSeller("MONTH");

        assertNotNull(result);
        assertEquals("Продавец Месяца", result.getName());
    }

    @Test
    void getMostProductiveSeller_WhenNoTransactions_ShouldReturnEmptyDTO() {
        when(transactionRepository.findMostProductiveSellerByTotalAmount(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        SellerDTO result = analyticsService.getMostProductiveSeller("DAY");

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getId());
    }

    @Test
    void findMostProductiveTimePeriod_WhenDaysIsZero_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> analyticsService.findMostProductiveTimePeriod(1L, 0));
    }

    @Test
    void findMostProductiveTimePeriod_WhenDaysIsNegative_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> analyticsService.findMostProductiveTimePeriod(1L, -5));
    }
}