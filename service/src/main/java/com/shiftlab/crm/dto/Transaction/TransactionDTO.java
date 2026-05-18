package com.shiftlab.crm.dto.transaction;

import com.shiftlab.crm.model.PaymentType;
import com.shiftlab.crm.model.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Schema(description = "Полная информация о транзакции")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TransactionDTO extends TransactionShortDTO {

    @Schema(description = "ID продавца")
    private Long sellerId;

    @Schema(description = "Тип оплаты")
    private PaymentType paymentType;

    public TransactionDTO(Transaction transaction) {
        super(transaction);
        this.sellerId = transaction.getSeller().getId();
        this.paymentType = transaction.getPaymentType();
    }
}
