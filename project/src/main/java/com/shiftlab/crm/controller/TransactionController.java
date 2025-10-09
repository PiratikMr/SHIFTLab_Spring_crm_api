package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.Transaction.TransactionDTO;
import com.shiftlab.crm.dto.Transaction.TransactionShortDTO;
import com.shiftlab.crm.model.Transaction;
import com.shiftlab.crm.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Получить список всех транзакций
    @GetMapping
    public ResponseEntity<List<TransactionShortDTO>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        List<TransactionShortDTO> transactions = transactionService.getTransactions(page, perPage);
        return ResponseEntity.ok(transactions);
    }

    // Получить информацию о конкретной транзакции
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        TransactionDTO transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    // Создать новую транзакцию. Принимаем ID продавца в URL.
    @PostMapping("/seller/{sellerId}")
    public ResponseEntity<TransactionDTO> createTransaction(
            @PathVariable Long sellerId,
            @RequestBody Transaction transaction) {
        TransactionDTO createdTransaction = transactionService.createTransaction(sellerId, transaction);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    // Получить все транзакции продавца
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsBySellerId(@PathVariable Long sellerId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsBySellerId(sellerId);
        return ResponseEntity.ok(transactions);
    }

    // Обновить транзакцию (редко используется)
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @RequestBody Transaction transactionDetails) {
        TransactionDTO updatedTransaction = transactionService.updateTransaction(id, transactionDetails);
        return ResponseEntity.ok(updatedTransaction);
    }

    // Удалить транзакцию
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}