package com.shiftlab.crm.dto.transaction;

import com.shiftlab.crm.model.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Краткая информация о транзакции")
@Data
@NoArgsConstructor
public class TransactionShortDTO {

    @Schema(description = "ID транзакции")
    private Long id;

    @Schema(description = "Сумма транзакции")
    private BigDecimal amount;

    @Schema(description = "Дата транзакции")
    private LocalDateTime transactionDate;

    public TransactionShortDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.transactionDate = transaction.getTransactionDate();
    }
}
