package com.shiftlab.crm.dto.Transaction;

import com.shiftlab.crm.model.Transaction;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionShortDTO {
    private Long id;
    private BigDecimal amount;

    public TransactionShortDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
    }
}
