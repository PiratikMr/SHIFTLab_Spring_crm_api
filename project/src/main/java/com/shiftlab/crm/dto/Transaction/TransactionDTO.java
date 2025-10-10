package com.shiftlab.crm.dto.Transaction;

import com.shiftlab.crm.model.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TransactionDTO extends TransactionShortDTO {
    private Long seller_id;
    private Transaction.PaymentType paymentType;

    public TransactionDTO(Transaction transaction) {
        super(transaction);
        this.seller_id = transaction.getSeller().getId();
        this.paymentType = transaction.getPaymentType();
    }
}
