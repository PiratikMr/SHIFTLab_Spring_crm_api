package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.CustomPage;
import com.shiftlab.crm.dto.SellerRequest;
import com.shiftlab.crm.dto.seller.SellerDTO;
import com.shiftlab.crm.dto.seller.SellerShortDTO;
import com.shiftlab.crm.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.shiftlab.crm.controller.ApiPaths.BASE;
import static com.shiftlab.crm.controller.ApiPaths.SELLERS;

@RestController
@RequestMapping(BASE + SELLERS)
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    public ResponseEntity<CustomPage<SellerShortDTO>> getSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        Page<SellerShortDTO> sellers = sellerService.getSellers(page, perPage);
        return ResponseEntity.ok(new CustomPage<>(sellers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerDTO> getSellerById(@PathVariable Long id) {
        return ResponseEntity.ok(sellerService.getSellerById(id));
    }

    @PostMapping
    public ResponseEntity<SellerShortDTO> createSeller(@Valid @RequestBody SellerRequest request) {
        return new ResponseEntity<>(sellerService.createSeller(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SellerDTO> updateSeller(@PathVariable Long id, @Valid @RequestBody SellerRequest request) {
        return ResponseEntity.ok(sellerService.updateSeller(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}
