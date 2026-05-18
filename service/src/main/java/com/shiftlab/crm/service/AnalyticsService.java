package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.seller.SellerDTO;
import com.shiftlab.crm.dto.seller.SellerShortDTO;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.model.PeriodType;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;
import com.shiftlab.crm.repository.SellerRepository;
import com.shiftlab.crm.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    public AnalyticsService(TransactionRepository transactionRepository, SellerRepository sellerRepository) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
    }

    @Transactional(readOnly = true)
    public Optional<SellerDTO> getMostProductiveSeller(PeriodType periodType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = switch (periodType) {
            case DAY -> now.toLocalDate().atStartOfDay();
            case MONTH -> now.toLocalDate().withDayOfMonth(1).atStartOfDay();
            case QUARTER -> {
                int startMonth = ((now.getMonthValue() - 1) / 3) * 3 + 1;
                yield now.toLocalDate().withMonth(startMonth).withDayOfMonth(1).atStartOfDay();
            }
            case YEAR -> now.toLocalDate().withDayOfYear(1).atStartOfDay();
        };

        return transactionRepository.findMostProductiveSellerByTotalAmount(startDate, now)
                .map(SellerDTO::new);
    }

    @Transactional(readOnly = true)
    public List<SellerShortDTO> getSellersWithTotalAmountLessThan(LocalDateTime startDate, LocalDateTime endDate, BigDecimal maxTotalAmount) {
        return sellerRepository.findSellersWithTotalAmountBelowWithCount(maxTotalAmount, startDate, endDate)
                .stream()
                .map(SellerShortDTO::new)
                .toList();
    }


    // Получение лучшего периода времени (диапазон дат) с выбранным размером в днях для переданного продавца.
    // Двухуказательный алгоритм O(N log N): сортируем все даты транзакций (с дублями),
    // затем за O(N) скользим окном ровно days календарнхы дней и считаем транзакции внутри.
    @Transactional(readOnly = true)
    public BestPeriodResult findMostProductiveTimePeriod(Long sellerId, int days) {
        if (days <= 0) throw new IllegalArgumentException("Количество дней должно быть больше 0");

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Продавец с ID " + sellerId + " не найден"));

        List<Transaction> transactions = transactionRepository.findBySeller(seller);

        if (transactions.isEmpty()) return new BestPeriodResult(null, null, 0);

        // Берём все даты транзакций (с дублями — несколько транзакций в один день сохраняются)
        List<LocalDate> sortedDates = transactions.stream()
                .map(t -> t.getTransactionDate().toLocalDate())
                .sorted()
                .toList();

        int n = sortedDates.size();
        int maxCount = 0;
        LocalDate bestStart = null;
        int left = 0;

        for (int right = 0; right < n; right++) {
            // Сдвигаем левый указатель, пока окно шире days календарных дней
            while (ChronoUnit.DAYS.between(sortedDates.get(left), sortedDates.get(right)) >= days) {
                left++;
            }
            int count = right - left + 1;
            if (count > maxCount) {
                maxCount = count;
                bestStart = sortedDates.get(left);
            }
        }

        //эксклюзивная правая граница календарного окна
        LocalDate bestEnd = bestStart != null ? bestStart.plusDays(days) : null;
        return new BestPeriodResult(bestStart, bestEnd, maxCount);
    }

    public record BestPeriodResult(LocalDate startDate, LocalDate endDate, int transactionCount) {
    }
}