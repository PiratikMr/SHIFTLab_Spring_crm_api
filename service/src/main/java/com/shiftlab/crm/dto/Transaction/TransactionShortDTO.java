package com.shiftlab.crm.dto.transaction;

import com.shiftlab.crm.model.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionShortDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime transactionDate;

    public TransactionShortDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.transactionDate = transaction.getTransactionDate();
    }
}
