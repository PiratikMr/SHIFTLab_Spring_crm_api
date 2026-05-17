package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.Seller.SellerDTO;
import com.shiftlab.crm.dto.Seller.SellerShortDTO;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;
import com.shiftlab.crm.repository.SellerRepository;
import com.shiftlab.crm.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    public AnalyticsService(TransactionRepository transactionRepository, SellerRepository sellerRepository) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
    }

    @Transactional(readOnly = true)
    public SellerDTO getMostProductiveSeller(String periodType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = switch (periodType.toUpperCase()) {
            case "DAY" -> now.toLocalDate().atStartOfDay();
            case "MONTH" -> now.toLocalDate().withDayOfMonth(1).atStartOfDay();
            case "QUARTER" -> {
                int startMonth = ((now.getMonthValue() - 1) / 3) * 3 + 1;
                yield now.toLocalDate().withMonth(startMonth).withDayOfMonth(1).atStartOfDay();
            }
            case "YEAR" -> now.toLocalDate().withDayOfYear(1).atStartOfDay();
            default -> throw new IllegalArgumentException("Неверный тип периода: " + periodType);
        };

        return transactionRepository.findMostProductiveSellerByTotalAmount(startDate, now)
                .map(SellerDTO::new)
                .orElse(new SellerDTO());
    }

    @Transactional(readOnly = true)
    public List<SellerShortDTO> getSellersWithTotalAmountLessThan(LocalDateTime startDate, LocalDateTime endDate, BigDecimal maxTotalAmount) {
        return sellerRepository.findSellersWithTotalAmountBelow(maxTotalAmount, startDate, endDate)
                .stream()
                .map(SellerShortDTO::new)
                .toList();
    }


    // *Получение лучшего периода времени (диапазон дат) с выбранным размером (в днях) для переданного продавца

    // Задача решается "скользящим окном" с помощью префиксных сумм.
    // Агрегируем транзакции по дням, строим префиксные суммы для O(1) расчета сумм диапазона.
    // Затем за O(N - D) (N - количество транзакций, D — число дней) находим максимальную сумму в окне фиксированного размера.
    @Transactional(readOnly = true)
    public BestPeriodResult findMostProductiveTimePeriod(Long sellerId, int days) {
        if (days <= 0) throw new IllegalArgumentException("Количество дней должно быть больше 0");

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Продавец с ID " + sellerId + " не найден"));

        List<Transaction> transactions = transactionRepository.findBySeller(seller);

        if (transactions.isEmpty()) return new BestPeriodResult(null, null, 0);

        // агрегируем транзакции по дням
        Map<LocalDate, Integer> dailyCounts = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().toLocalDate(),
                        Collectors.summingInt(t -> 1)
                ));

        // сортируем даты
        List<LocalDate> sortedDates = dailyCounts.keySet().stream().sorted()
                .toList();

        int n = sortedDates.size();

        // если данных меньше, чем размер окна, возвращаем все количество всех транзакций
        if (n <= days) {
            int totalTransactions = transactions.size();
            LocalDate start = sortedDates.getFirst();
            LocalDate end = sortedDates.getLast().plusDays(1);
            return new BestPeriodResult(start, end, totalTransactions);
        }


        int[] dailyTransactionCounts = new int[n];
        for (int i = 0; i < n; i++) {
            dailyTransactionCounts[i] = dailyCounts.get(sortedDates.get(i));
        }


        int[] prefixSums = new int[n];
        prefixSums[0] = dailyTransactionCounts[0];
        for (int i = 1; i < n; i++) {
            prefixSums[i] = prefixSums[i - 1] + dailyTransactionCounts[i];
        }

        int maxTransactions = 0;
        LocalDate bestStart = null;
        LocalDate bestEnd = null;

        for (int i = 0; i <= n - days; i++) {
            int j = i + days - 1;

            int currentCount = prefixSums[j] - (i > 0 ? prefixSums[i - 1] : 0);

            if (currentCount > maxTransactions) {
                maxTransactions = currentCount;
                bestStart = sortedDates.get(i);
                bestEnd = sortedDates.get(j);
            }
        }

        return new BestPeriodResult(bestStart, bestEnd != null ? bestEnd.plusDays(1) : null, maxTransactions);
    }

    public record BestPeriodResult(LocalDate startDate, LocalDate endDate, int transactionCount) {}
}