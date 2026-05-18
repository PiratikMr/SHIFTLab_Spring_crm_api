package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.TransactionRequest;
import com.shiftlab.crm.dto.transaction.TransactionDTO;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;
import com.shiftlab.crm.repository.SellerRepository;
import com.shiftlab.crm.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    public TransactionService(TransactionRepository transactionRepository, SellerRepository sellerRepository) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
    }

    @Transactional
    public TransactionDTO createTransaction(Long sellerId, TransactionRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Продавец с ID " + sellerId + " не найден для создания транзакции"));

        Transaction transaction = new Transaction();
        transaction.setSeller(seller);
        transaction.setAmount(request.getAmount());
        transaction.setPaymentType(request.getPaymentType());
        transaction.setTransactionDate(LocalDateTime.now());

        return new TransactionDTO(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public Page<TransactionDTO> getTransactions(int page, int perPage) {
        return transactionRepository.findAll(PageRequest.of(page, perPage))
                .map(TransactionDTO::new);
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Long id) {
        return new TransactionDTO(getPureTransactionById(id));
    }

    @Transactional(readOnly = true)
    public Page<TransactionDTO> getTransactionsBySellerId(Long sellerId, int page, int perPage) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Продавец с ID " + sellerId + " не найден"));

        Page<Transaction> transactions = transactionRepository.findBySeller(seller, PageRequest.of(page, perPage));

        return transactions.map(TransactionDTO::new);
    }

    @Transactional
    public TransactionDTO updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = getPureTransactionById(id);

        transaction.setAmount(request.getAmount());
        transaction.setPaymentType(request.getPaymentType());

        return new TransactionDTO(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = getPureTransactionById(id);
        transactionRepository.delete(transaction);
    }

    private Transaction getPureTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Транзакция с ID " + id + " не найдена"));
    }
}