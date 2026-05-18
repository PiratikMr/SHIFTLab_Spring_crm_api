package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.CustomPage;
import com.shiftlab.crm.dto.transaction.TransactionDTO;
import com.shiftlab.crm.dto.TransactionRequest;
import com.shiftlab.crm.service.TransactionService;
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
import static com.shiftlab.crm.controller.ApiPaths.SELLER_SUB;
import static com.shiftlab.crm.controller.ApiPaths.TRANSACTIONS;

@Tag(name = "Транзакции", description = "Управление транзакциями")
@RestController
@RequestMapping(BASE + TRANSACTIONS)
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Список всех транзакций")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @GetMapping
    public ResponseEntity<CustomPage<TransactionDTO>> getTransactions(
            @Parameter(description = "Номер страницы (с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Элементов на странице") @RequestParam(defaultValue = "10") int perPage
    ) {
        Page<TransactionDTO> transactions = transactionService.getTransactions(page, perPage);
        return ResponseEntity.ok(new CustomPage<>(transactions));
    }

    @Operation(summary = "Получить транзакцию по ID")
    @ApiResponse(responseCode = "200", description = "Транзакция найдена")
    @ApiResponse(responseCode = "404", description = "Транзакция не найдена", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @Operation(summary = "Создать транзакцию для продавца")
    @ApiResponse(responseCode = "201", description = "Транзакция создана")
    @ApiResponse(responseCode = "404", description = "Продавец не найден", content = @Content)
    @PostMapping(SELLER_SUB + "/{sellerId}")
    public ResponseEntity<TransactionDTO> createTransaction(
            @Parameter(description = "ID продавца") @PathVariable Long sellerId,
            @Valid @RequestBody TransactionRequest request) {
        return new ResponseEntity<>(transactionService.createTransaction(sellerId, request), HttpStatus.CREATED);
    }

    @Operation(summary = "Транзакции продавца")
    @ApiResponse(responseCode = "200", description = "Успешно")
    @ApiResponse(responseCode = "404", description = "Продавец не найден", content = @Content)
    @GetMapping(SELLER_SUB + "/{sellerId}")
    public ResponseEntity<CustomPage<TransactionDTO>> getTransactionsBySellerId(
            @Parameter(description = "ID продавца") @PathVariable Long sellerId,
            @Parameter(description = "Номер страницы (с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Элементов на странице") @RequestParam(defaultValue = "10") int perPage
    ) {
        Page<TransactionDTO> transactions = transactionService.getTransactionsBySellerId(sellerId, page, perPage);
        return ResponseEntity.ok(new CustomPage<>(transactions));
    }

    @Operation(summary = "Обновить транзакцию")
    @ApiResponse(responseCode = "200", description = "Транзакция обновлена")
    @ApiResponse(responseCode = "404", description = "Транзакция не найдена", content = @Content)
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request));
    }

    @Operation(summary = "Удалить транзакцию")
    @ApiResponse(responseCode = "204", description = "Транзакция удалена")
    @ApiResponse(responseCode = "404", description = "Транзакция не найдена", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
