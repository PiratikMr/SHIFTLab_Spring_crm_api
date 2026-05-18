package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.SellerRequest;
import com.shiftlab.crm.dto.seller.SellerDTO;
import com.shiftlab.crm.dto.seller.SellerShortDTO;
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
    public SellerShortDTO createSeller(SellerRequest request) {
        Seller seller = new Seller();
        seller.setName(request.getName());
        seller.setContactInfo(request.getContactInfo());
        seller.setRegistrationDate(LocalDateTime.now());
        return new SellerShortDTO(sellerRepository.save(seller));
    }

    @Transactional(readOnly = true)
    public Page<SellerShortDTO> getSellers(int page, int perPage) {
        return sellerRepository.findAllWithTransactionCount(PageRequest.of(page, perPage))
                .map(SellerShortDTO::new);
    }

    @Transactional(readOnly = true)
    public SellerDTO getSellerById(Long id) {
        return new SellerDTO(getPureSellerById(id));
    }

    @Transactional
    public SellerDTO updateSeller(Long id, SellerRequest request) {
        Seller seller = getPureSellerById(id);

        seller.setName(request.getName());
        seller.setContactInfo(request.getContactInfo());

        return new SellerDTO(sellerRepository.save(seller));
    }

    @Transactional
    public void deleteSeller(Long id) {
        Seller seller = getPureSellerById(id);
        seller.setDeleted(true);
        sellerRepository.save(seller);
    }

    private Seller getPureSellerById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продавец с ID " + id + " не найден"));
    }
}