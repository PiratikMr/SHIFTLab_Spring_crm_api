package com.shiftlab.crm.dto.transaction;

import com.shiftlab.crm.model.PaymentType;
import com.shiftlab.crm.model.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TransactionDTO extends TransactionShortDTO {
    private Long sellerId;
    private PaymentType paymentType;

    public TransactionDTO(Transaction transaction) {
        super(transaction);
        this.sellerId = transaction.getSeller().getId();
        this.paymentType = transaction.getPaymentType();
    }
}
