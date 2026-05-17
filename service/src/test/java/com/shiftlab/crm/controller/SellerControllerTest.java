package com.shiftlab.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiftlab.crm.dto.Seller.SellerDTO;
import com.shiftlab.crm.dto.Seller.SellerShortDTO;
import com.shiftlab.crm.dto.SellerRequest;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.service.SellerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SellerController.class)
public class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SellerService sellerService;

    private final String BASE_URL = "/api/sellers";

    @Test
    void getSellers_ShouldReturnPagedList_Success() throws Exception {
        SellerShortDTO dto = new SellerShortDTO();
        dto.setId(1L);
        dto.setName("Test Seller");
        dto.setContactInfo("test@test.com");
        dto.setRegistrationDate(LocalDateTime.now());
        dto.setTransactionsCount(0);

        Page<SellerShortDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        when(sellerService.getSellers(0, 10)).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("perPage", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Seller"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(sellerService, times(1)).getSellers(0, 10);
    }

    @Test
    void getSellerById_ShouldReturnSellerDTO_Success() throws Exception {
        SellerDTO dto = new SellerDTO();
        dto.setId(1L);
        dto.setName("Single Seller");
        dto.setContactInfo("info@example.com");
        dto.setRegistrationDate(LocalDateTime.now());
        dto.setTransactionsCount(0);
        dto.setTransactions(Collections.emptyList());

        when(sellerService.getSellerById(1L)).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Single Seller"))
                .andExpect(jsonPath("$.id").value(1));

        verify(sellerService, times(1)).getSellerById(1L);
    }

    @Test
    void createSeller_ShouldReturnCreatedSeller_Success() throws Exception {
        SellerRequest request = new SellerRequest();
        request.setName("New Seller");

        SellerShortDTO createdDto = new SellerShortDTO();
        createdDto.setId(1L);
        createdDto.setName("New Seller");

        when(sellerService.createSeller(any(SellerRequest.class))).thenReturn(createdDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Seller"))
                .andExpect(jsonPath("$.id").value(1));

        verify(sellerService, times(1)).createSeller(any(SellerRequest.class));
    }

    @Test
    void updateSeller_ShouldReturnUpdatedSeller_Success() throws Exception {
        SellerRequest request = new SellerRequest();
        request.setName("Updated Name");
        request.setContactInfo("new@contact.com");

        SellerDTO updatedDto = new SellerDTO();
        updatedDto.setId(1L);
        updatedDto.setName("Updated Name");
        updatedDto.setContactInfo("new@contact.com");
        updatedDto.setRegistrationDate(LocalDateTime.now());
        updatedDto.setTransactionsCount(0);
        updatedDto.setTransactions(Collections.emptyList());

        when(sellerService.updateSeller(eq(1L), any(SellerRequest.class))).thenReturn(updatedDto);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(sellerService, times(1)).updateSeller(eq(1L), any(SellerRequest.class));
    }

    @Test
    void deleteSeller_ShouldReturnNoContent_Success() throws Exception {
        doNothing().when(sellerService).deleteSeller(1L);

        mockMvc.perform(delete(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(sellerService, times(1)).deleteSeller(1L);
    }

    @Test
    void getSellerById_ShouldReturnNotFound_404() throws Exception {
        when(sellerService.getSellerById(999L)).thenThrow(new ResourceNotFoundException("Продавец не найден"));

        mockMvc.perform(get(BASE_URL + "/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Продавец не найден"));
    }

    @Test
    void createSeller_ShouldReturnBadRequest_WhenNameIsMissing() throws Exception {
        SellerRequest invalidRequest = new SellerRequest();
        invalidRequest.setName("");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Ошибка валидации данных. Подробности: name: Имя продавца не может быть пустым")));
    }
}
