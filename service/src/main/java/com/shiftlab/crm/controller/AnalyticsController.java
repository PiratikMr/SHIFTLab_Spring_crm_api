package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.seller.SellerDTO;
import com.shiftlab.crm.dto.seller.SellerShortDTO;
import com.shiftlab.crm.model.PeriodType;
import com.shiftlab.crm.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.shiftlab.crm.controller.ApiPaths.ANALYTICS;
import static com.shiftlab.crm.controller.ApiPaths.BASE;
import static com.shiftlab.crm.controller.ApiPaths.MOST_PRODUCTIVE_PERIOD;
import static com.shiftlab.crm.controller.ApiPaths.MOST_PRODUCTIVE_SELLER;
import static com.shiftlab.crm.controller.ApiPaths.SELLERS_BELOW_AMOUNT;

@RestController
@RequestMapping(BASE + ANALYTICS)
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping(MOST_PRODUCTIVE_SELLER + "/{periodType}")
    public ResponseEntity<SellerDTO> getMostProductiveSeller(@PathVariable String periodType) {
        Optional<SellerDTO> seller = analyticsService.getMostProductiveSeller(PeriodType.of(periodType));
        return ResponseEntity.of(seller);
    }

    @GetMapping(SELLERS_BELOW_AMOUNT)
    public ResponseEntity<List<SellerShortDTO>> getSellersWithTotalAmountLessThan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam BigDecimal maxTotalAmount) {

        List<SellerShortDTO> sellers = analyticsService.getSellersWithTotalAmountLessThan(startDate, endDate, maxTotalAmount);
        return ResponseEntity.ok(sellers);
    }

    @GetMapping(MOST_PRODUCTIVE_PERIOD + "/{sellerId}")
    public ResponseEntity<AnalyticsService.BestPeriodResult> getMostProductivePeriod(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "1") int days
    ) {
        AnalyticsService.BestPeriodResult result = analyticsService.findMostProductiveTimePeriod(sellerId, days);
        return ResponseEntity.ok(result);
    }
}
