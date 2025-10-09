package com.shiftlab.crm.service;

import com.shiftlab.crm.dto.Transaction.TransactionDTO;
import com.shiftlab.crm.dto.Transaction.TransactionShortDTO;
import com.shiftlab.crm.exception.ResourceNotFoundException;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;
import com.shiftlab.crm.repository.SellerRepository;
import com.shiftlab.crm.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    public TransactionService(TransactionRepository transactionRepository, SellerRepository sellerRepository) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
    }

    @Transactional
    public TransactionDTO createTransaction(Long sellerId, Transaction transaction) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Продавец с ID " + sellerId + " не найден для создания транзакции"));

        transaction.setSeller(seller);
        transaction.setTransactionDate(LocalDateTime.now());

        return new TransactionDTO(transactionRepository.save(transaction));
    }


    public List<TransactionShortDTO> getTransactions(int page, int perPage) {
        return transactionRepository.findAll(PageRequest.of(page, perPage)).stream()
                .map(TransactionShortDTO::new).collect(Collectors.toList());
    }

    private Transaction getPureTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Транзакция с ID " + id + " не найдена"));
    }



    public TransactionDTO getTransactionById(Long id) {
        return new TransactionDTO(getPureTransactionById(id));
    }

    public List<TransactionDTO> getTransactionsBySellerId(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Продавец с ID " + sellerId + " не найден"));
        return transactionRepository.findBySeller(seller).stream().map(TransactionDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public TransactionDTO updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = getPureTransactionById(id);

        transaction.setAmount(transactionDetails.getAmount());
        transaction.setPaymentType(transactionDetails.getPaymentType());

        return new TransactionDTO(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = getPureTransactionById(id);
        transactionRepository.delete(transaction);
    }
}