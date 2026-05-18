package com.shiftlab.crm.fixture;

import com.shiftlab.crm.dto.SellerRequest;
import com.shiftlab.crm.dto.TransactionRequest;
import com.shiftlab.crm.dto.seller.SellerDTO;
import com.shiftlab.crm.dto.seller.SellerShortDTO;
import com.shiftlab.crm.dto.transaction.TransactionDTO;
import com.shiftlab.crm.model.PaymentType;
import com.shiftlab.crm.model.Seller;
import com.shiftlab.crm.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

public final class TestDataFactory {

    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final Long DEFAULT_TRANSACTION_ID = 100L;
    public static final String DEFAULT_SELLER_NAME = "Тестовый Продавец";
    public static final String DEFAULT_CONTACT_INFO = "test@example.com";
    public static final BigDecimal DEFAULT_AMOUNT = new BigDecimal("123.45");

    private TestDataFactory() {
    }


    public static Seller seller(long id, String name) {
        Seller s = new Seller();
        s.setId(id);
        s.setName(name);
        s.setContactInfo(DEFAULT_CONTACT_INFO);
        s.setRegistrationDate(LocalDateTime.now());
        return s;
    }

    public static Seller seller(String name) {
        return seller(DEFAULT_SELLER_ID, name);
    }

    public static Seller seller() {
        return seller(DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME);
    }


    public static Transaction transaction(long id, Seller seller, BigDecimal amount, PaymentType paymentType) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setAmount(amount);
        t.setSeller(seller);
        t.setPaymentType(paymentType);
        t.setTransactionDate(LocalDateTime.now());
        return t;
    }

    public static Transaction transaction(Seller seller) {
        return transaction(DEFAULT_TRANSACTION_ID, seller, DEFAULT_AMOUNT, PaymentType.CASH);
    }

    public static Transaction transactionAt(LocalDateTime dateTime) {
        Transaction t = new Transaction();
        t.setTransactionDate(dateTime);
        return t;
    }


    public static SellerShortDTO sellerShortDTO(long id, String name) {
        SellerShortDTO dto = new SellerShortDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setContactInfo(DEFAULT_CONTACT_INFO);
        dto.setRegistrationDate(LocalDateTime.now());
        dto.setTransactionsCount(0);
        return dto;
    }

    public static SellerShortDTO sellerShortDTO(String name) {
        return sellerShortDTO(DEFAULT_SELLER_ID, name);
    }

    public static SellerShortDTO sellerShortDTO() {
        return sellerShortDTO(DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME);
    }


    public static SellerDTO sellerDTO(long id, String name) {
        SellerDTO dto = new SellerDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setContactInfo(DEFAULT_CONTACT_INFO);
        dto.setRegistrationDate(LocalDateTime.now());
        dto.setTransactionsCount(0);
        dto.setTransactions(Collections.emptyList());
        return dto;
    }

    public static SellerDTO sellerDTO(String name) {
        return sellerDTO(DEFAULT_SELLER_ID, name);
    }

    public static SellerDTO sellerDTO() {
        return sellerDTO(DEFAULT_SELLER_ID, DEFAULT_SELLER_NAME);
    }


    public static TransactionDTO transactionDTO(long id, Long sellerId, BigDecimal amount, PaymentType paymentType) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(id);
        dto.setAmount(amount);
        dto.setTransactionDate(LocalDateTime.now());
        dto.setSellerId(sellerId);
        dto.setPaymentType(paymentType);
        return dto;
    }

    public static TransactionDTO transactionDTO(Long sellerId) {
        return transactionDTO(DEFAULT_TRANSACTION_ID, sellerId, DEFAULT_AMOUNT, PaymentType.CASH);
    }

    public static TransactionDTO transactionDTO() {
        return transactionDTO(DEFAULT_SELLER_ID);
    }


    public static SellerRequest sellerRequest(String name, String contactInfo) {
        SellerRequest r = new SellerRequest();
        r.setName(name);
        r.setContactInfo(contactInfo);
        return r;
    }

    public static SellerRequest sellerRequest(String name) {
        return sellerRequest(name, DEFAULT_CONTACT_INFO);
    }


    public static TransactionRequest transactionRequest(BigDecimal amount, PaymentType paymentType) {
        TransactionRequest r = new TransactionRequest();
        r.setAmount(amount);
        r.setPaymentType(paymentType);
        return r;
    }

    public static TransactionRequest transactionRequest() {
        return transactionRequest(DEFAULT_AMOUNT, PaymentType.CASH);
    }
}
