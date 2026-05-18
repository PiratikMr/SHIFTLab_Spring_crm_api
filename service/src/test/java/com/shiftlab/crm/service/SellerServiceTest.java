package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.seller.SellerDTO;
import com.shiftlab.crm.dto.seller.SellerShortDTO;
import com.shiftlab.crm.dto.SellerRequest;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.fixture.TestDataFactory;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.repository.SellerCountProjection;
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

import java.time.LocalDateTime;
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
        SellerRequest request = TestDataFactory.sellerRequest("Новый Продавец");
        Seller savedSeller = TestDataFactory.seller("Новый Продавец");
        when(sellerRepository.save(any(Seller.class))).thenReturn(savedSeller);

        SellerShortDTO result = sellerService.createSeller(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        ArgumentCaptor<Seller> captor = ArgumentCaptor.forClass(Seller.class);
        verify(sellerRepository).save(captor.capture());
        assertNotNull(captor.getValue().getRegistrationDate());
        assertEquals("Новый Продавец", captor.getValue().getName());
    }

    @Test
    void getSellerById_WhenFound_ShouldReturnDTO() {
        Seller seller = TestDataFactory.seller("Тестовый");
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
        SellerCountProjection projection = mock(SellerCountProjection.class);
        when(projection.getId()).thenReturn(1L);
        when(projection.getName()).thenReturn("Тестовый");
        when(projection.getContactInfo()).thenReturn(null);
        when(projection.getRegistrationDate()).thenReturn(LocalDateTime.now());
        when(projection.getTransactionsCount()).thenReturn(0L);

        Page<SellerCountProjection> projPage = new PageImpl<>(Collections.singletonList(projection));
        when(sellerRepository.findAllWithTransactionCount(any(PageRequest.class))).thenReturn(projPage);

        Page<SellerShortDTO> result = sellerService.getSellers(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().getFirst().getId());
    }

    @Test
    void updateSeller_WhenFound_ShouldUpdateAndReturnDTO() {
        Seller existingSeller = TestDataFactory.seller("Старое Имя");
        SellerRequest request = TestDataFactory.sellerRequest("Новое Имя", "new@info.com");
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(existingSeller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(i -> i.getArguments()[0]);

        SellerDTO result = sellerService.updateSeller(1L, request);

        assertEquals("Новое Имя", result.getName());
        assertEquals("new@info.com", result.getContactInfo());
    }

    @Test
    void deleteSeller_WhenFound_ShouldSoftDelete() {
        Seller seller = TestDataFactory.seller();
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(Seller.class))).thenAnswer(i -> i.getArguments()[0]);

        sellerService.deleteSeller(1L);

        ArgumentCaptor<Seller> captor = ArgumentCaptor.forClass(Seller.class);
        verify(sellerRepository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().isDeleted());
        verify(sellerRepository, never()).delete(any());
    }

    @Test
    void deleteSeller_WhenNotFound_ShouldThrowException() {
        when(sellerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sellerService.deleteSeller(99L));
    }
}
