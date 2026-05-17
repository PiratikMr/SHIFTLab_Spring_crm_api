package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.CustomPage;
import com.shiftlab.crm.dto.Transaction.TransactionDTO;
import com.shiftlab.crm.dto.TransactionRequest;
import com.shiftlab.crm.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Получить список всех транзакций
    @GetMapping
    public ResponseEntity<CustomPage<TransactionDTO>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        Page<TransactionDTO> transactions = transactionService.getTransactions(page, perPage);
        return ResponseEntity.ok(new CustomPage<>(transactions));
    }

    // Получить информацию о конкретной транзакции
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        TransactionDTO transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    // Создать новую транзакцию
    @PostMapping("/seller/{sellerId}")
    public ResponseEntity<TransactionDTO> createTransaction(
            @PathVariable Long sellerId,
            @Valid @RequestBody TransactionRequest request) {
        TransactionDTO createdTransaction = transactionService.createTransaction(sellerId, request);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    // Получить все транзакции продавца
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<CustomPage<TransactionDTO>> getTransactionsBySellerId(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        Page<TransactionDTO> transactions = transactionService.getTransactionsBySellerId(sellerId, page, perPage);
        return ResponseEntity.ok(new CustomPage<>(transactions));
    }

    // Обновить транзакцию
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        TransactionDTO updatedTransaction = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(updatedTransaction);
    }

    // Удалить транзакцию
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}