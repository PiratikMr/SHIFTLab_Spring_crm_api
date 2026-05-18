package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.seller.SellerDTO;
import com.shiftlab.crm.dto.seller.SellerShortDTO;
import com.shiftlab.crm.model.PeriodType;
import com.shiftlab.crm.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Аналитика", description = "Аналитика по продавцам и транзакциям")
@RestController
@RequestMapping(BASE + ANALYTICS)
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Operation(summary = "Наиболее продуктивный продавец за период")
    @ApiResponse(responseCode = "200", description = "Продавец найден")
    @ApiResponse(responseCode = "404", description = "Нет транзакций за указанный период", content = @Content)
    @GetMapping(MOST_PRODUCTIVE_SELLER + "/{periodType}")
    public ResponseEntity<SellerDTO> getMostProductiveSeller(
            @Parameter(description = "Тип периода: DAY, MONTH, QUARTER, YEAR") @PathVariable String periodType
    ) {
        Optional<SellerDTO> seller = analyticsService.getMostProductiveSeller(PeriodType.of(periodType));
        return ResponseEntity.of(seller);
    }

    @Operation(summary = "Продавцы с суммой продаж ниже порога за период")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @GetMapping(SELLERS_BELOW_AMOUNT)
    public ResponseEntity<List<SellerShortDTO>> getSellersWithTotalAmountLessThan(
            @Parameter(description = "Начало периода (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Конец периода (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Максимальная сумма продаж") @RequestParam BigDecimal maxTotalAmount
    ) {
        List<SellerShortDTO> sellers = analyticsService.getSellersWithTotalAmountLessThan(startDate, endDate, maxTotalAmount);
        return ResponseEntity.ok(sellers);
    }

    @Operation(summary = "Наиболее продуктивный период продавца")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Продавец не найден", content = @Content)
    @GetMapping(MOST_PRODUCTIVE_PERIOD + "/{sellerId}")
    public ResponseEntity<AnalyticsService.BestPeriodResult> getMostProductivePeriod(
            @Parameter(description = "ID продавца") @PathVariable Long sellerId,
            @Parameter(description = "Размер окна в днях") @RequestParam(defaultValue = "1") int days
    ) {
        AnalyticsService.BestPeriodResult result = analyticsService.findMostProductiveTimePeriod(sellerId, days);
        return ResponseEntity.ok(result);
    }
}
