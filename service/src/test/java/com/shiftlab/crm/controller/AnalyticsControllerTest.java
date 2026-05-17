package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.Seller.SellerDTO;
import com.shiftlab.crm.dto.Seller.SellerShortDTO;
import com.shiftlab.crm.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
public class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    private final String BASE_URL = "/api/analytics";
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void getMostProductiveSeller_ShouldReturnSellerDTO_Success() throws Exception {
        String periodType = "MONTH";

        SellerDTO seller = new SellerDTO();
        seller.setId(1L);
        seller.setName("Top Seller");
        seller.setContactInfo("top@email.com");
        seller.setRegistrationDate(LocalDateTime.now());
        seller.setTransactionsCount(100);
        seller.setTransactions(Collections.emptyList());

        when(analyticsService.getMostProductiveSeller(periodType)).thenReturn(seller);

        mockMvc.perform(get(BASE_URL + "/most-productive-seller/{periodType}", periodType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Top Seller"))
                .andExpect(jsonPath("$.transactionsCount").value(100));
    }

    @Test
    void getSellersWithTotalAmountLessThan_ShouldReturnList_Success() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 2, 1, 0, 0);
        BigDecimal maxAmount = new BigDecimal("1000.00");

        SellerShortDTO seller = new SellerShortDTO();
        seller.setId(1L);
        seller.setName("Low Volume Seller");
        seller.setContactInfo("low@email.com");
        seller.setTransactionsCount(5);

        when(analyticsService.getSellersWithTotalAmountLessThan(any(LocalDateTime.class), any(LocalDateTime.class), eq(maxAmount)))
                .thenReturn(List.of(seller));

        mockMvc.perform(get(BASE_URL + "/sellers-below-amount")
                        .param("startDate", start.format(formatter))
                        .param("endDate", end.format(formatter))
                        .param("maxTotalAmount", maxAmount.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Low Volume Seller"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getMostProductivePeriod_ShouldReturnBestPeriodResult_Success() throws Exception {
        Long sellerId = 1L;
        int days = 7;

        AnalyticsService.BestPeriodResult result = new AnalyticsService.BestPeriodResult(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 7),
                15
        );
        when(analyticsService.findMostProductiveTimePeriod(sellerId, days)).thenReturn(result);

        mockMvc.perform(get(BASE_URL + "/most-productive-period/{sellerId}", sellerId)
                        .param("days", String.valueOf(days))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionCount").value(15))
                .andExpect(jsonPath("$.startDate").value("2024-05-01"));
    }


    @Test
    void getMostProductiveSeller_ShouldReturnBadRequest_WhenInvalidPeriodType() throws Exception {
        String invalidPeriodType = "WEEK";
        when(analyticsService.getMostProductiveSeller(invalidPeriodType))
                .thenThrow(new IllegalArgumentException("Неверный тип периода: " + invalidPeriodType));

        mockMvc.perform(get(BASE_URL + "/most-productive-seller/{periodType}", invalidPeriodType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Неверный тип периода")));
    }

    @Test
    void getMostProductivePeriod_ShouldReturnBadRequest_WhenDaysIsZero() throws Exception {
        when(analyticsService.findMostProductiveTimePeriod(1L, 0))
                .thenThrow(new IllegalArgumentException("Количество дней должно быть больше 0"));

        mockMvc.perform(get(BASE_URL + "/most-productive-period/1")
                        .param("days", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Количество дней")));
    }
}