package com.shiftlab.crm.dto;

import com.shiftlab.crm.model.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Сумма транзакции обязательна")
    @DecimalMin(value = "0", message = "Сумма транзакции не может быть отрицательной")
    private BigDecimal amount;

    @NotNull(message = "Тип оплаты обязателен")
    private Transaction.PaymentType paymentType;
}
