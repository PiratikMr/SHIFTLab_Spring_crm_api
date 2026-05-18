package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.CustomPage;
import com.shiftlab.crm.dto.SellerRequest;
import com.shiftlab.crm.dto.seller.SellerDTO;
import com.shiftlab.crm.dto.seller.SellerShortDTO;
import com.shiftlab.crm.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.shiftlab.crm.controller.ApiPaths.BASE;
import static com.shiftlab.crm.controller.ApiPaths.SELLERS;

@Tag(name = "Продавцы", description = "Управление продавцами")
@RestController
@RequestMapping(BASE + SELLERS)
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Operation(summary = "Список продавцов")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @GetMapping
    public ResponseEntity<CustomPage<SellerShortDTO>> getSellers(
            @Parameter(description = "Номер страницы (с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Элементов на странице") @RequestParam(defaultValue = "10") int perPage
    ) {
        Page<SellerShortDTO> sellers = sellerService.getSellers(page, perPage);
        return ResponseEntity.ok(new CustomPage<>(sellers));
    }

    @Operation(summary = "Получить продавца по ID")
    @ApiResponse(responseCode = "200", description = "Продавец найден")
    @ApiResponse(responseCode = "404", description = "Продавец не найден", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<SellerDTO> getSellerById(@PathVariable Long id) {
        return ResponseEntity.ok(sellerService.getSellerById(id));
    }

    @Operation(summary = "Создать продавца")
    @ApiResponse(responseCode = "201", description = "Продавец создан")
    @PostMapping
    public ResponseEntity<SellerShortDTO> createSeller(@Valid @RequestBody SellerRequest request) {
        return new ResponseEntity<>(sellerService.createSeller(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновить данные продавца")
    @ApiResponse(responseCode = "200", description = "Продавец обновлён")
    @ApiResponse(responseCode = "404", description = "Продавец не найден", content = @Content)
    @PutMapping("/{id}")
    public ResponseEntity<SellerDTO> updateSeller(@PathVariable Long id, @Valid @RequestBody SellerRequest request) {
        return ResponseEntity.ok(sellerService.updateSeller(id, request));
    }

    @Operation(summary = "Удалить продавца")
    @ApiResponse(responseCode = "204", description = "Продавец удалён")
    @ApiResponse(responseCode = "404", description = "Продавец не найден", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}
