package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.Seller.SellerDTO;
import com.shiftlab.crm.dto.Seller.SellerShortDTO;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;
    @InjectMocks
    private SellerService sellerService;

    @Test
    void createSeller_ShouldSaveAndReturnDTO() {
        Seller seller = new Seller();
        seller.setName("Новый Продавец");

        Seller savedSeller = new Seller();
        savedSeller.setId(1L);
        savedSeller.setName("Новый Продавец");

        when(sellerRepository.save(any(Seller.class))).thenReturn(savedSeller);

        SellerShortDTO result = sellerService.createSeller(seller);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);
        verify(sellerRepository).save(sellerCaptor.capture());
        assertNotNull(sellerCaptor.getValue().getRegistrationDate());
    }

    @Test
    void getSellerById_WhenFound_ShouldReturnDTO() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("Тестовый");
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        SellerDTO result = sellerService.getSellerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getSellerById_WhenNotFound_ShouldThrowException() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sellerService.getSellerById(1L));
    }

    @Test
    void getSellers_ShouldReturnPageOfDTOs() {
        Seller seller = new Seller();
        seller.setId(1L);
        Page<Seller> sellerPage = new PageImpl<>(Collections.singletonList(seller));
        when(sellerRepository.findAll(any(PageRequest.class))).thenReturn(sellerPage);

        Page<SellerShortDTO> result = sellerService.getSellers(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().getFirst().getId());
    }

    @Test
    void updateSeller_WhenFound_ShouldUpdateAndReturnDTO() {
        Seller existingSeller = new Seller();
        existingSeller.setId(1L);
        existingSeller.setName("Старое Имя");

        Seller details = new Seller();
        details.setName("Новое Имя");
        details.setContactInfo("new@info.com");

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(existingSeller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(i -> i.getArguments()[0]);

        SellerDTO result = sellerService.updateSeller(1L, details);

        assertEquals("Новое Имя", result.getName());
        assertEquals("new@info.com", result.getContactInfo());
    }

    @Test
    void deleteSeller_WhenFound_ShouldCallDelete() {
        Seller seller = new Seller();
        seller.setId(1L);
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        sellerService.deleteSeller(1L);

        verify(sellerRepository, times(1)).delete(seller);
    }
}