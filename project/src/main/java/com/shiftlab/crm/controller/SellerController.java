package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.Seller.SellerDTO;
import com.shiftlab.crm.dto.Seller.SellerShortDTO;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // Список всех продавцов
    @GetMapping
    public ResponseEntity<List<SellerShortDTO>> getSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        List<SellerShortDTO> sellers = sellerService.getSellers(page, perPage);
        return ResponseEntity.ok(sellers);
    }

    // Инфо о конкретном продавце
    @GetMapping("/{id}")
    public ResponseEntity<SellerDTO> getSellerById(@PathVariable Long id) {
        SellerDTO seller = sellerService.getSellerById(id);
        return ResponseEntity.ok(seller);
    }

    // Создать нового продавца
    @PostMapping
    public ResponseEntity<SellerShortDTO> createSeller(@RequestBody Seller seller) {
        SellerShortDTO createdSeller = sellerService.createSeller(seller);
        return new ResponseEntity<>(createdSeller, HttpStatus.CREATED);
    }

    // Обновить инфо о продавце
    @PutMapping("/{id}")
    public ResponseEntity<SellerDTO> updateSeller(@PathVariable Long id, @RequestBody SellerDTO sellerDetails) {
        SellerDTO updatedSeller = sellerService.updateSeller(id, sellerDetails);
        return ResponseEntity.ok(updatedSeller);
    }

    // Удалить продавца
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}