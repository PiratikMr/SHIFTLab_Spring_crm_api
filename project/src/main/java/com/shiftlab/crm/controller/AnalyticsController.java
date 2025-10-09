package com.shiftlab.crm.controller;

import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // Получить самого продуктивного продавца за период (DAY, MONTH, QUARTER, YEAR)
    @GetMapping("/most-productive-seller/{periodType}")
    public ResponseEntity<Seller> getMostProductiveSeller(@PathVariable String periodType) {
        Seller seller = analyticsService.getMostProductiveSeller(periodType);
        return ResponseEntity.ok(seller);
    }

    // Получить список продавцов с суммой меньше указанной
    @GetMapping("/sellers-below-amount")
    public ResponseEntity<List<Seller>> getSellersWithTotalAmountLessThan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam BigDecimal maxTotalAmount) {

        List<Seller> sellers = analyticsService.getSellersWithTotalAmountLessThan(startDate, endDate, maxTotalAmount);
        return ResponseEntity.ok(sellers);
    }

    // *Сложная задача: Получить самое продуктивное время продавца
    // Эту конечную точку следует реализовать после основного функционала.
}