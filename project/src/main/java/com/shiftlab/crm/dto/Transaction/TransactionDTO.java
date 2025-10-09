package com.shiftlab.crm.dto.Transaction;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.shiftlab.crm.model.Transaction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    @JsonUnwrapped
    private TransactionShortDTO transactionShortDTO;
    private Long seller_id;
    private Transaction.PaymentType paymentType;
    private LocalDateTime transactionDate;

    public TransactionDTO(Transaction transaction) {
        this.transactionShortDTO = new TransactionShortDTO(transaction);
        this.seller_id = transaction.getSeller().getId();
        this.paymentType = transaction.getPaymentType();
        this.transactionDate = transaction.getTransactionDate();
    }
}
