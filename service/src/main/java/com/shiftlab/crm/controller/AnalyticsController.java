package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.Seller.SellerDTO;
import com.shiftlab.crm.dto.Seller.SellerShortDTO;
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

    // Получить самого продуктивного продавца за период (day, month, quarter, year)
    @GetMapping("/most-productive-seller/{periodType}")
    public ResponseEntity<SellerDTO> getMostProductiveSeller(@PathVariable String periodType) {
        SellerDTO seller = analyticsService.getMostProductiveSeller(periodType);
        return ResponseEntity.ok(seller);
    }

    // Получить список продавцов с суммой меньше указанной
    @GetMapping("/sellers-below-amount")
    public ResponseEntity<List<SellerShortDTO>> getSellersWithTotalAmountLessThan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam BigDecimal maxTotalAmount) {

        List<SellerShortDTO> sellers = analyticsService.getSellersWithTotalAmountLessThan(startDate, endDate, maxTotalAmount);
        return ResponseEntity.ok(sellers);
    }

    // *Получение лучшего периода времени (диапазон дат) с выбранным размером (в днях) для переданного продавца
    @GetMapping("/most-productive-period/{sellerId}")
    public ResponseEntity<AnalyticsService.BestPeriodResult> getMostProductivePeriod(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "1") int days
    ) {
        AnalyticsService.BestPeriodResult result = analyticsService.findMostProductiveTimePeriod(sellerId, days);
        return ResponseEntity.ok(result);
    }
}