package com.shiftlab.crm.service;

import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.repository.SellerRepository;
import com.shiftlab.crm.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    public AnalyticsService(TransactionRepository transactionRepository, SellerRepository sellerRepository) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
    }

    public Seller getMostProductiveSeller(String periodType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;

        switch (periodType.toUpperCase()) {
            case "DAY":
                startDate = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "MONTH":
                startDate = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "QUARTER":
                // Получение начала квартала
                int currentMonth = now.getMonthValue();
                int quarter = (currentMonth - 1) / 3 + 1;
                int startMonth = (quarter - 1) * 3 + 1;
                startDate = now.withMonth(startMonth).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            case "YEAR":
                startDate = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                break;
            default:
                throw new IllegalArgumentException("Неверный тип периода: " + periodType);
        }

        return transactionRepository.findMostProductiveSellerByTotalAmount(startDate, now)
                .orElse(null);
    }

    public List<Seller> getSellersWithTotalAmountLessThan(LocalDateTime startDate, LocalDateTime endDate, BigDecimal maxTotalAmount) {
        List<Seller> allSellers = sellerRepository.findAll();

        return allSellers.stream()
                .filter(seller -> {
                    BigDecimal totalAmount = transactionRepository.sumTotalAmountBySellerAndDateRange(seller, startDate, endDate);
                    return totalAmount.compareTo(maxTotalAmount) < 0; // totalAmount < maxTotalAmount
                })
                .collect(Collectors.toList());
    }

    // Реализация Сложной задачи: Определение наилучшего периода времени (необязательно)
    // Эта задача требует более сложного алгоритма (например, скользящее окно), который
    // выходит за рамки простого примера.
}