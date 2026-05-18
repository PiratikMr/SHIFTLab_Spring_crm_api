package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.seller.SellerDTO;
import com.shiftlab.crm.dto.seller.SellerShortDTO;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.fixture.TestDataFactory;
import com.shiftlab.crm.model.PeriodType;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;
import com.shiftlab.crm.repository.SellerCountProjection;
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
        Seller seller = TestDataFactory.seller("Лучший Продавец");
        when(transactionRepository.findMostProductiveSellerByTotalAmount(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(seller));

        Optional<SellerDTO> result = analyticsService.getMostProductiveSeller(PeriodType.DAY);

        assertTrue(result.isPresent());
        assertEquals("Лучший Продавец", result.get().getName());
        assertEquals(1L, result.get().getId());
        verify(transactionRepository, times(1)).findMostProductiveSellerByTotalAmount(any(), any());
    }

    @Test
    void getMostProductiveSeller_WithInvalidPeriodType_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> PeriodType.of("OTHER"));
    }

    @Test
    void getSellersWithTotalAmountLessThan_ShouldReturnCorrectSellers() {
        SellerCountProjection projection = mock(SellerCountProjection.class);
        when(projection.getId()).thenReturn(1L);
        when(projection.getName()).thenReturn("Скромный продавец");
        when(projection.getTransactionsCount()).thenReturn(3L);
        when(sellerRepository.findSellersWithTotalAmountBelowWithCount(any(BigDecimal.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(projection));

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
        Seller seller = TestDataFactory.seller();
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findBySeller(seller)).thenReturn(Collections.emptyList());

        AnalyticsService.BestPeriodResult result = analyticsService.findMostProductiveTimePeriod(1L, 1);

        assertEquals(0, result.transactionCount());
        assertNull(result.startDate());
        assertNull(result.endDate());
    }

    @Test
    void findMostProductiveTimePeriod_WithTransactions_ShouldReturnBestPeriod() {
        Seller seller = TestDataFactory.seller();
        List<Transaction> transactions = List.of(
                TestDataFactory.transactionAt(LocalDateTime.of(2025, 10, 5, 10, 0)),
                TestDataFactory.transactionAt(LocalDateTime.of(2025, 10, 7, 12, 0)),
                TestDataFactory.transactionAt(LocalDateTime.of(2025, 10, 7, 14, 0))
        );
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findBySeller(seller)).thenReturn(transactions);

        // Окно 1 день: лучший день — Oct 7 с двумя транзакциями
        AnalyticsService.BestPeriodResult result = analyticsService.findMostProductiveTimePeriod(1L, 1);

        assertEquals(2, result.transactionCount());
        assertEquals(LocalDate.of(2025, 10, 7), result.startDate());
    }

    @Test
    void findMostProductiveTimePeriod_WhenWindowLargerThanData_ShouldReturnBestCalendarWindow() {
        Seller seller = TestDataFactory.seller();
        List<Transaction> transactions = List.of(
                TestDataFactory.transactionAt(LocalDateTime.of(2025, 10, 1, 10, 0)),
                TestDataFactory.transactionAt(LocalDateTime.of(2025, 10, 3, 12, 0))
        );
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.findBySeller(seller)).thenReturn(transactions);

        // Окно 5 дней: [Oct1, Oct5] содержит обе транзакции (Oct1 и Oct3)
        AnalyticsService.BestPeriodResult result = analyticsService.findMostProductiveTimePeriod(1L, 5);

        assertEquals(2, result.transactionCount());
        assertEquals(LocalDate.of(2025, 10, 1), result.startDate());
        assertEquals(LocalDate.of(2025, 10, 6), result.endDate()); // bestStart + days
    }

    @Test
    void getMostProductiveSeller_ForMonth_ShouldUseStartOfDay() {
        Seller seller = TestDataFactory.seller("Продавец Месяца");
        when(transactionRepository.findMostProductiveSellerByTotalAmount(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime startDate = invocation.getArgument(0);
                    assertEquals(0, startDate.getHour(), "startDate должен начинаться с 00:00");
                    assertEquals(0, startDate.getMinute());
                    assertEquals(0, startDate.getSecond());
                    assertEquals(1, startDate.getDayOfMonth(), "MONTH должен начинаться с 1-го числа");
                    return Optional.of(seller);
                });

        Optional<SellerDTO> result = analyticsService.getMostProductiveSeller(PeriodType.MONTH);

        assertTrue(result.isPresent());
        assertEquals("Продавец Месяца", result.get().getName());
    }

    @Test
    void getMostProductiveSeller_WhenNoTransactions_ShouldReturnEmptyOptional() {
        when(transactionRepository.findMostProductiveSellerByTotalAmount(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Optional<SellerDTO> result = analyticsService.getMostProductiveSeller(PeriodType.DAY);

        assertTrue(result.isEmpty());
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
