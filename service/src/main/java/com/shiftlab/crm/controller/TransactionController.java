package com.shiftlab.crm.controller;

import com.shiftlab.crm.dto.CustomPage;
import com.shiftlab.crm.dto.transaction.TransactionDTO;
import com.shiftlab.crm.dto.TransactionRequest;
import com.shiftlab.crm.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.shiftlab.crm.controller.ApiPaths.BASE;
import static com.shiftlab.crm.controller.ApiPaths.SELLER_SUB;
import static com.shiftlab.crm.controller.ApiPaths.TRANSACTIONS;

@RestController
@RequestMapping(BASE + TRANSACTIONS)
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<CustomPage<TransactionDTO>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        Page<TransactionDTO> transactions = transactionService.getTransactions(page, perPage);
        return ResponseEntity.ok(new CustomPage<>(transactions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping(SELLER_SUB + "/{sellerId}")
    public ResponseEntity<TransactionDTO> createTransaction(
            @PathVariable Long sellerId,
            @Valid @RequestBody TransactionRequest request) {
        return new ResponseEntity<>(transactionService.createTransaction(sellerId, request), HttpStatus.CREATED);
    }

    @GetMapping(SELLER_SUB + "/{sellerId}")
    public ResponseEntity<CustomPage<TransactionDTO>> getTransactionsBySellerId(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        Page<TransactionDTO> transactions = transactionService.getTransactionsBySellerId(sellerId, page, perPage);
        return ResponseEntity.ok(new CustomPage<>(transactions));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
