package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.Seller.SellerDTO;
import com.shiftlab.crm.dto.Seller.SellerShortDTO;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.repository.SellerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<SellerShortDTO> getSellers(int page, int perPage) {
        List<Seller> sellerList = sellerRepository.findAll(PageRequest.of(page, perPage)).getContent();
        return sellerList.stream().map(SellerShortDTO::new).collect(Collectors.toList());
    }

    private Seller getPureSellerById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продавец с ID " + id + " не найден"));
    }

    public SellerDTO getSellerById(Long id) {
        return new SellerDTO(getPureSellerById(id));
    }

    @Transactional
    public SellerDTO updateSeller(Long id, SellerDTO sellerDetails) {
        Seller seller = getPureSellerById(id);

        seller.setName(sellerDetails.getSellerShortDTO().getName());
        seller.setContactInfo(sellerDetails.getSellerShortDTO().getContactInfo());

        return new SellerDTO(sellerRepository.save(seller));
    }

    @Transactional
    public void deleteSeller(Long id) {
        Seller seller = getPureSellerById(id);
        sellerRepository.delete(seller);
    }
}