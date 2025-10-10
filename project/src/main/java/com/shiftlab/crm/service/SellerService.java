package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.Seller.SellerDTO;
import com.shiftlab.crm.dto.Seller.SellerShortDTO;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.repository.SellerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Transactional
    public SellerShortDTO createSeller(Seller seller) {
        seller.setRegistrationDate(LocalDateTime.now());
        return new SellerShortDTO(sellerRepository.save(seller));
    }

    public Page<SellerShortDTO> getSellers(int page, int perPage) {
        Page<Seller> sellerPage = sellerRepository.findAll(PageRequest.of(page, perPage));
        return sellerPage.map(SellerShortDTO::new);
    }

    public SellerDTO getSellerById(Long id) {
        return new SellerDTO(getPureSellerById(id));
    }

    @Transactional
    public SellerDTO updateSeller(Long id, Seller sellerDetails) {
        Seller seller = getPureSellerById(id);

        seller.setName(sellerDetails.getName());
        seller.setContactInfo(sellerDetails.getContactInfo());

        return new SellerDTO(sellerRepository.save(seller));
    }

    @Transactional
    public void deleteSeller(Long id) {
        Seller seller = getPureSellerById(id);
        sellerRepository.delete(seller);
    }




    private Seller getPureSellerById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продавец с ID " + id + " не найден"));
    }
}